package io.github.vkb24312.ImageMod;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws IOException {
        JPanel panel = new JPanel();

        BufferedImage in = chooseImage(panel);
        if (in == null) System.exit(0);
        panel.removeAll();
        panel.setVisible(false);

        int filter = new Optioner(new String[]{"Saturate", "RGB-ize", "Average-ize", "Random-ify"}).getOption();

        BufferedImage out;

        if(filter==0) out = saturate(in);
        else if(filter==1) out = colorize(in);
        else if(filter==2) out = averageizer(in);
        else if(filter==3) out = randomGray(in);
        else out = in;

        File outFile = suitableOutput(chooseOutputDirectory(panel), "output", ".png");

        ImageIO.write(out, getType(outFile), outFile);
    }

    private static BufferedImage randomGray(BufferedImage in){
        BufferedImage out = new BufferedImage(in.getWidth(), in.getHeight(), in.getType());

        for(int x = 0; x < in.getWidth(); x++){
            for (int y = 0; y < in.getHeight(); y++){
                Color inColour = new Color(in.getRGB(x, y));

                int sum = inColour.getRed()+inColour.getGreen()+inColour.getBlue();

                Random r = new Random();
                int or,og,ob;
                do {
                    double rr = r.nextDouble();
                    double rg = r.nextDouble();
                    double rb = r.nextDouble();

                    double rs = rr + rg + rb;

                    or = (int) ((rr / rs) * sum);
                    og = (int) ((rg / rs) * sum);
                    ob = (int) ((rb / rs) * sum);
                } while (or>255 || og>255 || ob>255);

                out.setRGB(x, y, new Color(or, og, ob).getRGB());
            }
        }
        return out;
    }

    private static BufferedImage saturate(BufferedImage image) {
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

    private static BufferedImage colorize(BufferedImage image) {
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color xyColor = new Color(image.getRGB(x, y));

                int r = xyColor.getRed();
                int g = xyColor.getGreen();
                int b = xyColor.getBlue();

                Color chosen;

                if(r>g && r>b) chosen = new Color(r, 0, 0);
                else if(g>r && g>b) chosen = new Color(0, g, 0);
                else if(b>g && b>r) chosen = new Color(0, 0, b);
                else chosen = Color.BLACK;

                out.setRGB(x, y, chosen.getRGB());
            }
        }

        return out;
    }

    private static BufferedImage averageizer(BufferedImage image){
        BigInteger totalR = new BigInteger("0");
        BigInteger totalG = new BigInteger("0");
        BigInteger totalB = new BigInteger("0");
        BigInteger totalPixels = new BigInteger(Long.toString((long)image.getHeight()*(long)image.getHeight()));

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                Color currentColor = new Color(image.getRGB(x, y));

                totalR = totalR.add(new BigInteger(Integer.toString(currentColor.getRed())));
                totalG = totalG.add(new BigInteger(Integer.toString(currentColor.getGreen())));
                totalB = totalB.add(new BigInteger(Integer.toString(currentColor.getBlue())));

                totalPixels = totalPixels.add(new BigInteger("1"));
            }
        }

        int averageR = Integer.parseInt(totalR.divide(totalPixels).toString());
        int averageG = Integer.parseInt(totalG.divide(totalPixels).toString());
        int averageB = Integer.parseInt(totalB.divide(totalPixels).toString());

        Color ac = new Color(averageR, averageG, averageB);
        BufferedImage out = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                out.setRGB(x, y, ac.getRGB());
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
            newName = preferredName + " " + i;
            i++;
        }

        return new File(directory, newName+extension);
    }
}

class Optioner extends JFrame{
    private int result = -1;
    private ArrayList<String> options = new ArrayList<>();

    Optioner(String[] options){
        Collections.addAll(this.options, options);
    }

    int getOption(){
        if(result>=0) return result;

        JPanel panel = new JPanel();
        this.setVisible(true);
        this.add(panel);
        this.setSize(options.size()*100, 100);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(screenSize.width/2-this.getSize().width/2, screenSize.height/2-this.getSize().height/2);

        final JButton[] buttons = new JButton[options.size()];

        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton(options.get(i));
        }

        for (JButton button : buttons) {
            panel.add(button);
        }

        for (final JButton button : buttons) {
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < options.size(); i++) {
                        if(button.getText().equals(options.get(i))) result = i;
                    }
                }
            });
        }

        while(result<0){try{Thread.sleep(5);}catch(InterruptedException ignore){}}
        dispose();
        return result;
    }
}