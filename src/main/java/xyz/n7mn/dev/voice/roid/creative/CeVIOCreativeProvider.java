package xyz.n7mn.dev.voice.roid.creative;

import xyz.n7mn.dev.CeVIOBuilder;
import xyz.n7mn.dev.CeVIOJava;
import xyz.n7mn.dev.data.enums.PlatformType;
import xyz.n7mn.dev.voice.roid.RoidProvider;

public class CeVIOCreativeProvider extends RoidProvider {
    private final static CeVIOJava cevioCreative = new CeVIOBuilder()
            .setPlatformType(PlatformType.CEVIO_CREATIVE_STUDIO)
            .setAutoStart(true)
            .create();

    @Override
    public void stop() {

    }
}
