package com.eds.client.desktop;

import com.badlogic.gdx.Gdx;
import com.eds.client.MyEdsClient;
import com.eds.client.Platform;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DesktopPlatform implements Platform {

    @Override
    public void chooseImage(FileListener listener) {
        JFileChooser fileOpen = new JFileChooser();

        // Get array of available formats
        String[] suffices = ImageIO.getReaderFileSuffixes();

        // Add a file filter for each one
        Gdx.app.log(MyEdsClient.LOG_TAG, "Supported image extensions: ");
        for (int i = 0; i < suffices.length; i++) {
            FileFilter filter = new FileNameExtensionFilter(suffices[i] + " files", suffices[i]);
            Gdx.app.log(MyEdsClient.LOG_TAG, "filter = " + filter);
            fileOpen.addChoosableFileFilter(filter);
        }

        // Show dialog
        int ret = fileOpen.showDialog(null, "Open an image");
        if (ret == JFileChooser.APPROVE_OPTION) {
            listener.fileChosen(fileOpen.getSelectedFile().getAbsolutePath());
        } else {
            listener.fileChosen(null);
        }
    }

    @Override
    public void showImage(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            ImageIcon icon = new ImageIcon(img);
            JLabel label = new JLabel(icon);
            JOptionPane.showMessageDialog(null, label);
        } catch (IOException e) {
            Gdx.app.error(MyEdsClient.LOG_TAG, "Error showing image!", e);
        }
    }
}
