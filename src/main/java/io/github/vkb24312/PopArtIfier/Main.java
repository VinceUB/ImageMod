package io.github.vkb24312.PopArtIfier;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.xuggler.io.XugglerIO;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.video.translator.FImageToMBFImageVideoTranslator;
import org.openimaj.video.xuggle.XuggleVideo;
import org.openimaj.video.xuggle.XuggleVideoWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        //region Get the FileNames of the images
        final JPanel panel = new JPanel();

        final JFileChooser jfk = new JFileChooser();
        panel.add(jfk);

        jfk.setDialogTitle("Choose your image");
        jfk.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || getType(f).toLowerCase().equals("jpg") || getType(f).toLowerCase().equals("jpeg") || getType(f).toLowerCase().equals("png") || getType(f).toLowerCase().equals("gif") || getType(f).toLowerCase().equals("bmp") || getType(f).toLowerCase().equals("wbmp");
            }

            @Override
            public String getDescription() {
                return "Music Files";
            }
        });

        int out = jfk.showOpenDialog(panel);

        if(out==JFileChooser.APPROVE_OPTION){
            panel.remove(jfk);

            JFileChooser jfc = new JFileChooser();
            panel.add(jfc);
            jfc.setDialogTitle("Choose the output's directory");
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int dOut = jfc.showOpenDialog(panel);

            if(dOut==JFileChooser.APPROVE_OPTION){
                BufferedImage imageOut = imageConvert(ImageIO.read(jfk.getSelectedFile()));
                File fileOut = new File(jfc.getSelectedFile().toString() + "/output.png");
                ImageIO.write(imageOut, getType(fileOut), fileOut);
            } else return;

        } else {
            return;
        }

        /*
        Scanner in = new Scanner(System.in);
        in.useDelimiter("\n");

        System.out.println("Full path of image/video");
        File inFile = new File(in.next());
        if(!inFile.exists()) {
            System.out.println("File does not exist");
            main(args);
            System.exit(0);
        }
        BufferedImage inImage = ImageIO.read(inFile);

        System.out.println("Full path of output");
        File outFile = new File(in.next());

        if(outFile.exists()) {
            System.out.println("File exists");
            main(args);
            System.exit(0);
        }

        BufferedImage outImage;

        //endregion

        if(getType(inFile).toLowerCase().equals("jpg") || getType(inFile).toLowerCase().equals("jpeg") || getType(inFile).toLowerCase().equals("png") || getType(inFile).toLowerCase().equals("gif") || getType(inFile).toLowerCase().equals("bmp") || getType(inFile).toLowerCase().equals("wbmp")) {

            outImage = imageConvert(inImage);
            ImageIO.write(outImage, getType(outFile), outFile);
        }

        else {
            System.out.println("Please use a valid image type file");
            System.exit(1);
        }*/
    }

    private static BufferedImage imageConvert(BufferedImage image){
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color inColor = new Color(image.getRGB(x, y));

                int r = 0;
                int g = 0;
                int b = 0;

                if(inColor.getRed()>=128) r = 255;
                if(inColor.getGreen()>=128) g = 255;
                if(inColor.getBlue()>=128) b = 255;

                Color outColor = new Color(r, g, b);

                out.setRGB(x, y, outColor.getRGB());
            }
        }

        return out;
    }

    private static void videoConvert(XuggleVideo video, File output){
        BufferedImage[] frames = new BufferedImage[(int) video.countFrames()];

        video.seekToBeginning();

        for (int i = 0; i < frames.length; i++) {
            frames[i] = imageConvert(ImageUtilities.createBufferedImage(video.getNextFrame()));
        }

        FImage[] out = new FImage[frames.length];

        for (int i = 0; i < out.length; i++) {
            out[i] = ImageUtilities.createFImage(frames[i]);
        }

        XuggleVideoWriter xvw = new XuggleVideoWriter("tempfile.mp4", video.getWidth(), video.getHeight(), video.getFPS());

        for (int i = 0; i < out.length; i++) {
            xvw.addFrame(ImageUtilities.createMBFImage(ImageUtilities.createBufferedImage(out[i]), false));
        }

        xvw.close();
    }


    private static String getType(File file){
        String fileType = "";
        int i = file.getPath().lastIndexOf('.');
        if (i > 0) {
            fileType = file.getPath().substring(i+1);
        }

        return fileType;
    }
}
