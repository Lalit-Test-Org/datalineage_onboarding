package com.datalineage.kafka.integration.service;

import com.datalineage.kafka.integration.dto.KafkaConnectionConfig;
import com.datalineage.kafka.integration.entity.KafkaConnectionEntity;
import com.datalineage.kafka.integration.entity.KafkaTopicEntity;
import com.datalineage.kafka.integration.repository.KafkaConnectionRepository;
import com.datalineage.kafka.integration.repository.KafkaTopicRepository;

import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for managing Kafka connections and metadata discovery
 */
@Service
@Transactional
public class KafkaConnectionService {
    
    @Autowired
    private KafkaConnectionRepository connectionRepository;
    
    @Autowired
    private KafkaTopicRepository topicRepository;
    
    @Autowired
    private EncryptionService encryptionService;
    
    @Autowired
    private SchemaRegistryClient schemaRegistryClient;
    
    /**
     * Test Kafka connection
     */
    public boolean testConnection(KafkaConnectionConfig config) {
        try (AdminClient adminClient = createAdminClient(config)) {
            // Simple test - try to list topics
            ListTopicsResult topicsResult = adminClient.listTopics();
            topicsResult.names().get(); // This will throw if connection fails
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Save Kafka connection configuration
     */
    public KafkaConnectionEntity saveConnection(KafkaConnectionConfig config) {
        KafkaConnectionEntity entity = new KafkaConnectionEntity();
        entity.setConnectionId(config.getConnectionId());
        entity.setBootstrapServers(config.getBootstrapServers());
        entity.setSecurityProtocol(config.getSecurityProtocol());
        entity.setSaslMechanism(config.getSaslMechanism());
        entity.setSaslUsername(config.getSaslUsername());
        
        // Encrypt sensitive data
        if (config.getSaslPassword() != null) {
            entity.setSaslPasswordEncrypted(encryptionService.encrypt(config.getSaslPassword()));
        }
        if (config.getKeystorePassword() != null) {
            entity.setKeystorePasswordEncrypted(encryptionService.encrypt(config.getKeystorePassword()));
        }
        if (config.getTruststorePassword() != null) {
            entity.setTruststorePasswordEncrypted(encryptionService.encrypt(config.getTruststorePassword()));
        }
        if (config.getSchemaRegistryPassword() != null) {
            entity.setSchemaRegistryPasswordEncrypted(encryptionService.encrypt(config.getSchemaRegistryPassword()));
        }
        
        entity.setKeystoreLocation(config.getKeystoreLocation());
        entity.setTruststoreLocation(config.getTruststoreLocation());
        entity.setSchemaRegistryUrl(config.getSchemaRegistryUrl());
        entity.setSchemaRegistryUsername(config.getSchemaRegistryUsername());
        entity.setConnectionTimeout(config.getConnectionTimeout());
        entity.setSessionTimeout(config.getSessionTimeout());
        entity.setRequestTimeout(config.getRequestTimeout());
        entity.setIsActive(true);
        
        // Test the connection and update status
        boolean connectionStatus = testConnection(config);
        entity.setConnectionStatus(connectionStatus ? "CONNECTED" : "FAILED");
        entity.setLastTested(LocalDateTime.now());
        
        return connectionRepository.save(entity);
    }
    
    /**
     * Get all active connections
     */
    public List<KafkaConnectionEntity> getActiveConnections() {
        return connectionRepository.findByIsActiveTrue();
    }
    
    /**
     * Get connection by ID
     */
    public Optional<KafkaConnectionEntity> getConnection(String connectionId) {
        return connectionRepository.findByConnectionId(connectionId);
    }
    
    /**
     * Discover topics for a connection
     */
    public List<KafkaTopicEntity> discoverTopics(String connectionId) {
        Optional<KafkaConnectionEntity> connectionOpt = connectionRepository.findByConnectionId(connectionId);
        if (connectionOpt.isEmpty()) {
            throw new RuntimeException("Connection not found: " + connectionId);
        }
        
        KafkaConnectionEntity connection = connectionOpt.get();
        KafkaConnectionConfig config = createConfigFromEntity(connection);
        
        try (AdminClient adminClient = createAdminClient(config)) {
            // Get topic names
            Set<String> topicNames = adminClient.listTopics().names().get();
            
            // Get topic descriptions
            Map<String, TopicDescription> topicDescriptions = adminClient.describeTopics(topicNames).all().get();
            
            // Get topic configurations
            List<ConfigResource> configResources = topicNames.stream()
                .map(name -> new ConfigResource(ConfigResource.Type.TOPIC, name))
                .collect(Collectors.toList());
            Map<ConfigResource, Config> topicConfigs = adminClient.describeConfigs(configResources).all().get();
            
            List<KafkaTopicEntity> topics = new ArrayList<>();
            
            for (String topicName : topicNames) {
                TopicDescription description = topicDescriptions.get(topicName);
                ConfigResource configResource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
                Config config1 = topicConfigs.get(configResource);
                
                KafkaTopicEntity topic = createTopicEntity(connection, topicName, description, config1);
                topics.add(topic);
            }
            
            // Save all topics
            topicRepository.saveAll(topics);
            return topics;
            
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Failed to discover topics for connection: " + connectionId, e);
        }
    }
    
    /**
     * Delete connection
     */
    public void deleteConnection(String connectionId) {
        Optional<KafkaConnectionEntity> connectionOpt = connectionRepository.findByConnectionId(connectionId);
        if (connectionOpt.isPresent()) {
            KafkaConnectionEntity connection = connectionOpt.get();
            // Delete associated topics first
            topicRepository.deleteByKafkaConnectionId(connection.getId());
            // Delete connection
            connectionRepository.deleteByConnectionId(connectionId);
        }
    }
    
    private AdminClient createAdminClient(KafkaConnectionConfig config) {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, config.getRequestTimeout() * 1000);
        
        if (!"PLAINTEXT".equals(config.getSecurityProtocol())) {
            props.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, config.getSecurityProtocol());
            
            if (config.getSaslMechanism() != null) {
                props.put("sasl.mechanism", config.getSaslMechanism());
                if (config.getSaslUsername() != null && config.getSaslPassword() != null) {
                    props.put("sasl.jaas.config", 
                        "org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"" + config.getSaslUsername() + "\" " +
                        "password=\"" + config.getSaslPassword() + "\";");
                }
            }
            
            if (config.getKeystoreLocation() != null) {
                props.put("ssl.keystore.location", config.getKeystoreLocation());
                props.put("ssl.keystore.password", config.getKeystorePassword());
            }
            
            if (config.getTruststoreLocation() != null) {
                props.put("ssl.truststore.location", config.getTruststoreLocation());
                props.put("ssl.truststore.password", config.getTruststorePassword());
            }
        }
        
        return AdminClient.create(props);
    }
    
    private KafkaConnectionConfig createConfigFromEntity(KafkaConnectionEntity entity) {
        KafkaConnectionConfig config = new KafkaConnectionConfig();
        config.setConnectionId(entity.getConnectionId());
        config.setBootstrapServers(entity.getBootstrapServers());
        config.setSecurityProtocol(entity.getSecurityProtocol());
        config.setSaslMechanism(entity.getSaslMechanism());
        config.setSaslUsername(entity.getSaslUsername());
        
        // Decrypt sensitive data
        if (entity.getSaslPasswordEncrypted() != null) {
            config.setSaslPassword(encryptionService.decrypt(entity.getSaslPasswordEncrypted()));
        }
        if (entity.getKeystorePasswordEncrypted() != null) {
            config.setKeystorePassword(encryptionService.decrypt(entity.getKeystorePasswordEncrypted()));
        }
        if (entity.getTruststorePasswordEncrypted() != null) {
            config.setTruststorePassword(encryptionService.decrypt(entity.getTruststorePasswordEncrypted()));
        }
        if (entity.getSchemaRegistryPasswordEncrypted() != null) {
            config.setSchemaRegistryPassword(encryptionService.decrypt(entity.getSchemaRegistryPasswordEncrypted()));
        }
        
        config.setKeystoreLocation(entity.getKeystoreLocation());
        config.setTruststoreLocation(entity.getTruststoreLocation());
        config.setSchemaRegistryUrl(entity.getSchemaRegistryUrl());
        config.setSchemaRegistryUsername(entity.getSchemaRegistryUsername());
        config.setConnectionTimeout(entity.getConnectionTimeout());
        config.setSessionTimeout(entity.getSessionTimeout());
        config.setRequestTimeout(entity.getRequestTimeout());
        
        return config;
    }
    
    private KafkaTopicEntity createTopicEntity(KafkaConnectionEntity connection, String topicName, 
                                             TopicDescription description, Config config) {
        KafkaTopicEntity topic = new KafkaTopicEntity();
        topic.setTopicName(topicName);
        topic.setKafkaConnectionId(connection.getId());
        
        if (description != null) {
            topic.setPartitions(description.partitions().size());
            if (!description.partitions().isEmpty()) {
                TopicPartitionInfo partition = description.partitions().get(0);
                topic.setReplicationFactor((short) partition.replicas().size());
            }
        }
        
        if (config != null) {
            config.entries().forEach(entry -> {
                switch (entry.name()) {
                    case "cleanup.policy":
                        topic.setCleanupPolicy(entry.value());
                        break;
                    case "retention.ms":
                        if (entry.value() != null) {
                            topic.setRetentionMs(Long.parseLong(entry.value()));
                        }
                        break;
                    case "retention.bytes":
                        if (entry.value() != null) {
                            topic.setRetentionBytes(Long.parseLong(entry.value()));
                        }
                        break;
                    case "segment.ms":
                        if (entry.value() != null) {
                            topic.setSegmentMs(Long.parseLong(entry.value()));
                        }
                        break;
                    case "segment.bytes":
                        if (entry.value() != null) {
                            topic.setSegmentBytes(Long.parseLong(entry.value()));
                        }
                        break;
                    case "compression.type":
                        topic.setCompressionType(entry.value());
                        break;
                    case "min.insync.replicas":
                        if (entry.value() != null) {
                            topic.setMinInsyncReplicas(Integer.parseInt(entry.value()));
                        }
                        break;
                }
            });
        }
        
        return topic;
    }
}