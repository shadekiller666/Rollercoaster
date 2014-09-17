package robomuss.rc.tracks.extra;

import net.minecraft.client.model.ModelBase;
import robomuss.rc.block.te.TileEntityTrack;
import robomuss.rc.entity.EntityTrainDefault;
import robomuss.rc.tracks.TrackType;

public class TrackExtraAirLauncher extends TrackExtra {

	public TrackExtraAirLauncher(String name, ModelBase model, Object[] recipe, int amount, TrackType... allowedTrackTypes) {
		super(name, model, recipe, amount, allowedTrackTypes);
	}
	
	@Override
	public void applyEffectToTrain(TileEntityTrack te, EntityTrainDefault entity) {
		entity.speed += 0.2f;
	}
}
