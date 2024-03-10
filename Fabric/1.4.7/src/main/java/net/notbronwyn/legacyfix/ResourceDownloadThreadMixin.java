package net.notbronwyn.legacyfix;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.ResourceDownloadThread;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.net.URL;

@Mixin(ResourceDownloadThread.class)
public class ResourceDownloadThreadMixin extends Thread {
    @Shadow
    private void method_800(File file, String string) {}
    @Shadow
    private boolean field_886;
    @Shadow
    private Minecraft client;
    @Shadow
    public File gameFolder;
    @Shadow
    private void method_801(URL uRL, File file, long l) {}
    public void downloadAndInstallResource(URL baseURL, String resourcePath, long l, int i, String hash) {
        try {
            int path1 = resourcePath.indexOf("/");
            String path = resourcePath.substring(0, path1);
            if (!path.equals("sound") && !path.equals("newsound")) {
                if (i != 1) {
                    return;
                }
            } else if (i != 0) {
                return;
            }
            File file = new File(this.gameFolder, resourcePath);
            if (!file.exists() || file.length() != l) {
                file.getParentFile().mkdirs();
                this.method_801(new URL(baseURL+hash.substring(0,2)+"/"+hash), file, l);
                if (this.field_886) {
                    return;
                }
            }
            this.client.method_2934(resourcePath, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Overwrite
    public void run() {
        try {
            URL url = new URL("https://gist.githubusercontent.com/notbronwyn/d2652de51d116f56cdbf5fbdc700e66f/raw/ad434a155897ebe2a90b4a294f2e83dfc0af5fad/MinecraftResources.xml");
            URL newAssetUrl = new URL("https://resources.download.minecraft.net/");
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            Document document = builder.parse(url.openStream());
            NodeList elems = document.getElementsByTagName("Contents");

            for(int int1 = 0; int1 < 2; ++int1) {
                for(int int2 = 0; int2 < elems.getLength(); ++int2) {
                    Node content1 = elems.item(int2);
                    if (content1.getNodeType() == 1) {
                        Element content = (Element)content1;
                        String path = content.getElementsByTagName("Key").item(0).getChildNodes().item(0).getNodeValue();
                        try {
                            String hash = ((Element)content.getElementsByTagName("Hash").item(0)).getChildNodes().item(0).getNodeValue();
                            long size = Long.parseLong(((Element)content.getElementsByTagName("Size").item(0)).getChildNodes().item(0).getNodeValue());
                            if(size > 0L) {
                                this.downloadAndInstallResource(newAssetUrl, path, size, int1, hash);
                                if(this.field_886) {
                                    return;
                                }
                            }
                        }
                        catch (NullPointerException e) {
                            // This just means its a directory
                            // we are chilling
                        }
                    }
                }
            }
        } catch (Exception var13) {
            this.method_800(this.gameFolder, "");
            var13.printStackTrace();
        }

    }
}
