package com.dtstack.engine.master.router;

import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MyTypeExcludeFilter extends TypeExcludeFilter {

    private final String RedisAutoConfigurationName = "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration";
    private final String RedisRepositoriesAutoConfigurationName = "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration";


    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        String className = metadataReader.getClassMetadata().getClassName();
        return RedisAutoConfigurationName.equals(className) || RedisRepositoriesAutoConfigurationName.equals(className);
    }
}