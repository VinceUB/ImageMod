package io.github.vkb24312.PopArtIfier;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        JPanel panel = new JPanel();

        BufferedImage in = chooseImage(panel);
        if (in == null) System.exit(0);
        panel.removeAll();

        BufferedImage out = imageConvert(in);

        File outFile = suitableOutput(chooseOutputDirectory(panel), "output", ".png");

        ImageIO.write(out, getType(outFile), outFile);
    }

    private static BufferedImage imageConvert(BufferedImage image) {
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color inColor = new Color(image.getRGB(x, y));

                int r = 0;
                int g = 0;
                int b = 0;

                if (inColor.getRed() >= 128) r = 255;
                if (inColor.getGreen() >= 128) g = 255;
                if (inColor.getBlue() >= 128) b = 255;

                Color outColor = new Color(r, g, b);

                out.setRGB(x, y, outColor.getRGB());
            }
        }

        return out;
    }


    private static String getType(File file){
        String fileType = "";
        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            fileType = file.getPath().substring(i+1);
        }

        return fileType;
    }

    private static BufferedImage chooseImage(Component parent) throws IOException{
        JFileChooser imageChooser = new JFileChooser();
        imageChooser.setDialogTitle("Choose the input image");

        imageChooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() ||
                        getType(f).toLowerCase().equals("jpg") ||
                        getType(f).toLowerCase().equals("jpeg") ||
                        getType(f).toLowerCase().equals("png") ||
                        getType(f).toLowerCase().equals("gif") ||
                        getType(f).toLowerCase().equals("bmp") ||
                        getType(f).toLowerCase().equals("wbmp");
            }

            @Override
            public String getDescription() {
                return "Supported Image Files (jpg, jpeg, png, gif, bmp, wbmp)";
            }
        });

        int result = imageChooser.showOpenDialog(parent);

        if(result==JFileChooser.APPROVE_OPTION){
            return ImageIO.read(imageChooser.getSelectedFile());
        } else {
            return null;
        }
    }

    private static File chooseOutputDirectory(Component parent){ //returns directory
        JFileChooser dirChooser = new JFileChooser();
        dirChooser.setDialogTitle("Choose the output directory");
        dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int result = dirChooser.showOpenDialog(parent);

        if(result==JFileChooser.APPROVE_OPTION){
            return dirChooser.getSelectedFile();
        } else {
            return null;
        }
    }

    private static File suitableOutput(File directory, String preferredName, String extension){ //Extension with a dot
        if(!new File(directory, preferredName+extension).exists())
            return new File(directory, preferredName+extension); //Check if already available

        String newName = preferredName;
        int i = 2;

        while(new File(directory, newName+extension).exists()){
            newName = preferredName + " " + i + extension;
            i++;
        }

        return new File(directory, newName+extension);
    }
}
