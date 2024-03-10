package net.notbronwyn.legacyfix;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import net.minecraft.client.render.BufferedImageSkinProvider;
import net.minecraft.client.texture.PlayerSkinTexture;

@Environment(EnvType.CLIENT)
@Mixin(targets = {"net.minecraft.client.class_526"})
public abstract class TextureDownloaderMixin extends Thread {
	@Shadow
	private final PlayerSkinTexture field_1875;
	@Shadow
	private final BufferedImageSkinProvider field_1874;
	@Shadow
	private final String field_1873;
	TextureDownloaderMixin(PlayerSkinTexture playerSkinTexture, String skinUrl, BufferedImageSkinProvider bufferedImageSkinProvider) {
		this.field_1875 = playerSkinTexture;
		this.field_1873 = skinUrl;
		this.field_1874 = bufferedImageSkinProvider;
	}
	@Overwrite
	public void run() {
		HttpURLConnection con = null;
		try {
			URL url = new URL(this.field_1873.replace("skins.minecraft.net","betacraft.uk"));
			con = (HttpURLConnection)url.openConnection();
			con.connect();
			if (con.getResponseCode()  == 400) { return; }
			if (this.field_1874 == null) {
				this.field_1875.field_1869 = ImageIO.read(con.getInputStream());
			} else {
				this.field_1875.field_1869 = this.field_1874.parseSkin(ImageIO.read(con.getInputStream()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			con.disconnect();
		}
	}
}
