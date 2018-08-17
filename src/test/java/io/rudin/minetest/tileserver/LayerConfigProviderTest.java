package io.rudin.minetest.tileserver;

import io.rudin.minetest.tileserver.config.LayerConfig;
import io.rudin.minetest.tileserver.provider.LayerConfigProvider;
import org.junit.Assert;
import org.junit.Test;

public class LayerConfigProviderTest {

    @Test
    public void test() throws Exception {

        LayerConfigProvider provider = new LayerConfigProvider();

        LayerConfig layerConfig = provider.get();

        Assert.assertNotNull(layerConfig);
        Assert.assertNotNull(layerConfig.layers);
        Assert.assertEquals(1, layerConfig.layers.size());
        Assert.assertEquals(0, layerConfig.layers.get(0).id);
        Assert.assertEquals("Base", layerConfig.layers.get(0).name);
        Assert.assertEquals(-16, layerConfig.layers.get(0).from);
        Assert.assertEquals(160, layerConfig.layers.get(0).to);

    }

}
