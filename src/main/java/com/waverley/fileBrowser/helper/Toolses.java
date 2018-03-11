package com.waverley.fileBrowser.helper;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


@Component
public class Toolses {

    //Create - preview
    public void saveScaledImage(InputStream in, String directoryWithFilename) throws IOException {


        try  {
            BufferedImage sourceImage = ImageIO.read(new BufferedInputStream(in));
            String outputFile = directoryWithFilename;


            int width = sourceImage.getWidth();
            int height = sourceImage.getHeight();


            new File(directoryWithFilename).createNewFile();

            if (width > height) {

                float extraSize = height - 200;

                float percentHight = (extraSize / height) * 200;

                float percentWidth = width - ((width / 200) * percentHight);

                float heightS;
                if (height < 201) {
                    heightS = height;
                } else {
                    heightS = 200 / (percentWidth / percentHight);
                }
                float widthS;
                if (width < 201) {
                    widthS = width;
                } else {
                    widthS = 200;
                }
                BufferedImage img = new BufferedImage((int) widthS, (int) heightS, BufferedImage.TYPE_INT_RGB);

                Graphics2D g = img.createGraphics();
                g.drawImage(sourceImage, 0, 0, (int) widthS, (int) heightS, null);
                g.dispose();
//// TODO: 10/3/2017 code should work with ather image formats (not noly jpg)
                ImageIO.write(img, "JPG", new File(outputFile));

            } else {

                float extraSize = width - 200;
                float percentWidth = (extraSize / width) * 200;

                float percentHight = height - ((height / 200) * percentWidth);
                float heightS;
                if (height < 201) {
                    heightS = height;
                } else {
                    heightS = 200;
                }
                float widthS;
                if (width < 201) {
                    widthS = width;
                } else {
                    widthS = 200 / (percentHight / percentWidth);
                }
                BufferedImage img = null;
                try {
                    img = new BufferedImage((int) widthS, (int) heightS, BufferedImage.TYPE_INT_RGB);
                } catch (NegativeArraySizeException e) {
                    e.printStackTrace();
                }
                Graphics2D g = img.createGraphics();
                g.drawImage(sourceImage, 0, 0, (int) widthS, (int) heightS, null);
                g.dispose();
                ImageIO.write(img, "JPG", new File(outputFile));
            }
            in.close();
            //traying to avoid error with heap
            sourceImage.flush();
            sourceImage = null;
        } catch (Exception e) {
            e.printStackTrace();
            createDefoultPreview(directoryWithFilename);
        }finally {
            in.close();
        }

//        }

    }

    public void createDefoultPreview(String directoryWithFilename) throws IOException {

        String path = Toolses.class.getClassLoader().getResource("defPreview.png").getPath();
        File sourceLocalfile = new File(path);
        FileInputStream inputStream = new FileInputStream(sourceLocalfile);
        File destinationLocalFile = new File(directoryWithFilename);
        Files.copy(inputStream, destinationLocalFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

//    public void writingNewFile(byte[] bytes, SmbFileOutputStream smbFileDestOutputStream) throws IOException {
//
//            smbFileDestOutputStream.write(bytes);
//            smbFileDestOutputStream.flush();
//            smbFileDestOutputStream.close();
//    }
//
//    public int[] calculateSizeOfThePreview(int hight, int width) {
//
//        int ratio = hight / width;
//
//        if (ratio > 1) {
//            hight = 100;
//            width = 100 / ratio;
//        } else {
//            width = 100;
//            hight = 100 / ratio;
//        }
//        return new int[]{hight, width};
//    }

}
