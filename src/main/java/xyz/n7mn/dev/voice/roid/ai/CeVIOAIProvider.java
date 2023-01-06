package xyz.n7mn.dev.voice.roid.ai;

import xyz.n7mn.dev.CeVIOBuilder;
import xyz.n7mn.dev.CeVIOJava;
import xyz.n7mn.dev.data.enums.PlatformType;
import xyz.n7mn.dev.voice.roid.RoidProvider;

import java.security.Provider;

public class CeVIOAIProvider extends RoidProvider {
    private final static CeVIOJava cevioAI = new CeVIOBuilder()
            .setPlatformType(PlatformType.CEVIO_AI)
            .setAutoStart(true)
            .create();

    @Override
    public void stop() {

    }
}
