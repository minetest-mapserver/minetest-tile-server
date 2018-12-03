package io.rudin.minetest.tileserver.service;

import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.util.zip.DataFormatException;

public interface MapBlockRenderService {
    void render(int fromY, int toY, int mapBlockX, int mapBlockZ, Graphics graphics, int scale) throws IllegalArgumentException, DataFormatException, ExecutionException;
}
