package com.letswork.crm.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.letswork.crm.serviceImpl.S3ServiceMCP;

@RestController
@RequestMapping("/mcp")
public class McpController {

    private final S3ServiceMCP s3Service;

    public McpController(S3ServiceMCP s3Service) {
        this.s3Service = s3Service;
    }

//    @GetMapping
//    public ResponseEntity<Map<String, Object>> getServerMetadata() {
//        Map<String, Object> metadata = Map.of(
//            "name", "KnowledgeBaseMCP",
//            "version", "1.0.0",
//            "description", "Custom MCP server streaming knowledge base from S3",
//            "transport", "SSE"
//        );
//        return ResponseEntity.ok(metadata);
//    }
//
//    @GetMapping("/tools")
//    public ResponseEntity<Map<String, Object>> listTools() {
//        Map<String, Object> fetchTool = Map.of(
//            "name", "fetch_knowledge_base",
//            "description", "Fetches a knowledge base file from AWS S3 for the agent.",
//            "input_schema", Map.of(
//                "type", "object",
//                "properties", Map.of(
//                    "key", Map.of("type", "string", "description", "S3 object key to fetch")
//                ),
//                "required", List.of("key")
//            )
//        );
//
//        Map<String, Object> streamTool = Map.of(
//            "name", "stream_knowledge_base",
//            "description", "Streams knowledge base content live via SSE.",
//            "input_schema", Map.of(
//                "type", "object",
//                "properties", Map.of(
//                    "key", Map.of("type", "string", "description", "S3 object key to stream")
//                ),
//                "required", List.of("key")
//            )
//        );
//
//        return ResponseEntity.ok(Map.of("tools", List.of(fetchTool, streamTool)));
//    }
//
//    @PostMapping("/tools/fetch_knowledge_base")
//    public ResponseEntity<Map<String, Object>> fetchKnowledgeBase() {
//        String key = "Refrigerator.pdf";
//        try {
//            String content = s3Service.getPdfAsText(key);
//            Map<String, Object> response = new HashMap<>();
//            response.put("content", content);
//            response.put("source", key);
//            response.put("timestamp", new Date().toString());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(Map.of(
//                "error", "Failed to fetch file from S3",
//                "message", e.getMessage()
//            ));
//        }
//    }
//
//    @GetMapping(value = "/tools/stream_knowledge_base", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ServerSentEvent<Map<String, Object>>> streamKnowledgeBase() {
//        String key = "Refrigerator.pdf";
//
//        try {
//            String content = s3Service.getPdfAsText(key);
//            String[] chunks = content.split("(?<=\\.)");
//
//            return Flux.fromArray(chunks)
//                    .delayElements(Duration.ofMillis(200))
//                    .map(chunk -> ServerSentEvent.<Map<String, Object>>builder()
//                            .event("message")
//                            .data(Map.of(
//                                    "chunk", chunk.trim(),
//                                    "source", key,
//                                    "timestamp", new Date().toString()
//                            ))
//                            .build())
//                    .concatWith(Flux.just(ServerSentEvent.<Map<String, Object>>builder()
//                            .event("complete")
//                            .data(Map.of("status", "done"))
//                            .build()));
//
//        } catch (Exception e) {
//            return Flux.just(ServerSentEvent.<Map<String, Object>>builder()
//                    .event("error")
//                    .data(Map.of("message", e.getMessage()))
//                    .build());
//        }
//    }
    
    
    
    
    
    
    
    
 // ---- 1. Base /mcp endpoint ----
    // ElevenLabs calls this to connect/handshake
    @PostMapping
    public ResponseEntity<Map<String, Object>> connect() {
        Map<String, Object> metadata = Map.of(
            "name", "KnowledgeBaseMCP",
            "version", "1.0.0",
            "description", "Custom MCP server using Streamable HTTP transport",
            "transport", "streamable_http"
        );
        return ResponseEntity.ok(metadata);
    }

    // ---- 2. List available tools ----
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> listTools() {
        Map<String, Object> fetchTool = Map.of(
            "name", "fetch_knowledge_base",
            "description", "Fetches a knowledge base file from AWS S3 for the agent.",
            "input_schema", Map.of(
                "type", "object",
                "properties", Map.of(
                    "key", Map.of("type", "string", "description", "S3 object key to fetch")
                ),
                "required", List.of("key")
            )
        );

        return ResponseEntity.ok(Map.of("tools", List.of(fetchTool)));
    }

    // ---- 3. Implement the tool logic ----
    @PostMapping("/tools/fetch_knowledge_base")
    public ResponseEntity<Map<String, Object>> fetchKnowledgeBase(@RequestBody Map<String, Object> body) {
        String key = (String) body.getOrDefault("key", "Refrigerator.pdf");
        try {
            String content = s3Service.getPdfAsText(key);
            Map<String, Object> response = new HashMap<>();
            response.put("content", content);
            response.put("source", key);
            response.put("timestamp", new Date().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "Failed to fetch file from S3",
                "message", e.getMessage()
            ));
        }
    }
    
    
}