package com.letswork.crm.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.letswork.crm.entities.LetsWorkCentre;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.typeMap(LetsWorkCentre.class, LetsWorkCentre.class)
              .addMappings(m -> m.skip(LetsWorkCentre::setImages));

        return mapper;
    }
}
