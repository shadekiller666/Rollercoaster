package robomuss.rc.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;

import org.apache.commons.io.FileUtils;

import robomuss.rc.exception.TrackStyleModelNotFoundException;
import robomuss.rc.track.TrackHandler;
import robomuss.rc.track.piece.TrackPiece;
import robomuss.rc.track.style.TrackStyle;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import cpw.mods.fml.common.FMLLog;

public class JSONHandler {

	private static String[] defaults = {
		"corkscrew", "body_slide", "body_slide_tunnel", "wooden_hybrid_ibox", "wooden_hybrid_topper"
	};
	
	public static void loadTrackStyles() throws IOException, TrackStyleModelNotFoundException, JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		File dir = new File("rollercoaster/track-styles/");
		if(!dir.exists()) {
			dir.mkdirs();
		}
		
		ClassLoader loader = JSONHandler.class.getClassLoader();
		for(String string : defaults) {
			File folder = new File("rollercoaster/track-styles/" + string + "/");
			if(!folder.exists()) {
				folder.mkdirs();
			}
			FileUtils.copyURLToFile(loader.getResource("assets/rc/trackStyles/" + string + ".json"), new File("rollercoaster/track-styles/" + string + ".json"));
			
			try {
				FileUtils.copyURLToFile(loader.getResource("assets/rc/models/trackStyles/" + string + "/standard.tcn"), new File("rollercoaster/track-styles/" + string + "/standard.tcn"));
				FileUtils.copyURLToFile(loader.getResource("assets/rc/models/trackStyles/" + string + "/large.tcn"), new File("rollercoaster/track-styles/" + string + "/large.tcn"));
				FileUtils.copyURLToFile(loader.getResource("assets/rc/models/trackStyles/" + string + "/extended.tcn"), new File("rollercoaster/track-styles/" + string + "/extended.tcn"));
				FileUtils.copyURLToFile(loader.getResource("assets/rc/models/trackStyles/" + string + "/corner.tcn"), new File("rollercoaster/track-styles/" + string + "/corner.tcn"));
				FMLLog.info("[Rollercoaster] Successfully copied track models for the '" + string + "' track style");
			}
			catch(IOException e) {
				throw new TrackStyleModelNotFoundException(e.getMessage().substring(e.getMessage().lastIndexOf("trackStyles/")), e.getMessage());
			}
		}
		
		loadJSONFilesFromDir(dir);
	}

	private static void loadJSONFilesFromDir(File dir) throws JsonIOException, JsonSyntaxException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		for(File file : dir.listFiles()) {
			if(file.getName().contains(".json")) {
				JsonParser parser = new JsonParser();
				JsonObject obj = (JsonObject) parser.parse(new FileReader(file));
				JsonArray whitelistedPiecesArray = obj.getAsJsonArray("Whitelisted Pieces");
				
				String name = obj.get("Name").getAsString();
				
				/*String standard = obj.get("Standard Model") != null ? obj.get("Standard Model").getAsString() : "corkscrew/Standard";
				String large = obj.get("Large Model") != null ? obj.get("Large Model").getAsString() : "corkscrew/Large";
				String extended = obj.get("Extended Model") != null ? obj.get("Extended Model").getAsString() : "corkscrew/Extended";
				String corner = obj.get("Corner Model") != null ? obj.get("Corner Model").getAsString() : "corkscrew/Corner";*/
				
				ArrayList<TrackPiece> whitelistedPieces = new ArrayList<TrackPiece>();
				
				StringBuilder sb = new StringBuilder();
				
				for(JsonElement element : whitelistedPiecesArray) {
					if(element.isJsonObject()) {
						JsonObject whitelistedPiece = element.getAsJsonObject();
						whitelistedPieces.add(TrackHandler.findTrackType(whitelistedPiece.get("Piece").getAsString()));
						sb.append(whitelistedPiece.get("Piece") + ", ");
					}
				}
				
				String fileName = file.getName().substring(0, file.getName().lastIndexOf(".json"));
				ClassLoader loader = JSONHandler.class.getClassLoader();
				FileUtils.copyFile(new File("rollercoaster/track-styles/" + fileName + "/standard.tcn"), new File(loader.getResource("assets/rc/models/trackStyles/" + fileName + "/standard.tcn").getPath()));
				FileUtils.copyFile(new File("rollercoaster/track-styles/" + fileName + "/large.tcn"), new File(loader.getResource("assets/rc/models/trackStyles/" + fileName + "/large.tcn").getPath()));
				FileUtils.copyFile(new File("rollercoaster/track-styles/" + fileName + "/extended.tcn"), new File(loader.getResource("assets/rc/models/trackStyles/" + fileName + "/extended.tcn").getPath()));
				FileUtils.copyFile(new File("rollercoaster/track-styles/" + fileName + "/corner.tcn"), new File(loader.getResource("assets/rc/models/trackStyles/" + fileName + "/corner.tcn").getPath()));
				
				TrackStyle style = new TrackStyle(file.getName().substring(0, file.getName().lastIndexOf(".json")));
				
				style.localizedName = name;
				
				style.standard = AdvancedModelLoader.loadModel(new ResourceLocation("rc", "models/trackStyles/" + fileName + "/standard.tcn"));
				style.large = AdvancedModelLoader.loadModel(new ResourceLocation("rc", "models/trackStyles/" + fileName + "/large.tcn"));
				style.extended = AdvancedModelLoader.loadModel(new ResourceLocation("rc", "models/trackStyles/" + fileName + "/extended.tcn"));
				style.corner = AdvancedModelLoader.loadModel(new ResourceLocation("rc", "models/trackStyles/" + fileName + "/corner.tcn"));
				
				style.whitelistedPieces = whitelistedPieces;
				
				style.riddenUsingCart = true;
				
				TrackHandler.styles.add(style);
				
				FMLLog.info("[Rollercoaster] Loaded track style: '" + name + "', Whitelisted Pieces: " + sb.toString());
			}
		}
	}
}
