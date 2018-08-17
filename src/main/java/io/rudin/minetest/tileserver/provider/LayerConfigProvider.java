package io.rudin.minetest.tileserver.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.rudin.minetest.tileserver.config.LayerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Singleton
public class LayerConfigProvider implements Provider<LayerConfig> {

    private static final Logger logger = LoggerFactory.getLogger(LayerConfigProvider.class);

    private static final String CLASSPATH_LOCATION = "/layers.json";
    private static final String FILE_LOCATION = "layers.json";

    public LayerConfigProvider() throws Exception {

        InputStream layersInput = LayerConfigProvider.class.getResourceAsStream(CLASSPATH_LOCATION);

        File file = new File(FILE_LOCATION);
        if (file.isFile()){
            logger.info("Reading layer config from file: {}", file.getAbsolutePath());
            layersInput = new FileInputStream(file);
        } else {
            logger.info("Using default layer config");
        }

        ObjectMapper mapper = new ObjectMapper();
        this.layers = mapper.readValue(layersInput, LayerConfig.class);

        layersInput.close();
    }

    private LayerConfig layers;

    @Override
    public LayerConfig get() {
        return layers;
    }
}
