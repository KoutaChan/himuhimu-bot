package xyz.n7mn.dev.yomiage;

import com.sun.jna.*;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Testing {

    //todo: customizable
    private static final String path_64 = "C:\\Program Files\\AHS\\VOICEROID2";
    private static final String path_32 = "C:\\Program Files (x86)\\AHS\\VOICEROID2";

    public interface VoiceRoid2 extends Library, StdCallLibrary {

        static VoiceRoid2 getInstance() {
            return Native.load(path_64 + "\\aitalked.dll", VoiceRoid2.class);
        }

        //VoiceRoid2 INSTANCE = Native.load(path_64 + "\\aitalked.dll", VoiceRoid2.class);

        int AITalkAPI_Init(AITalk_TConfig config);
        int AITalkAPI_End();
        int AITalkAPI_VoiceLoad(String voice);
        int AITalkAPI_LangClear();
        int AITalkAPI_LangLoad(String lang);
        int AITalkAPI_GetParam(AITalk_TTtsParam param, IntByReference size);
        int AITalkAPI_GetParam(Pointer pointer, IntByReference size);
        int AITalkAPI_GetParam(AITalk_TTtsParam param, int size);
        int AITalkAPI_SetParam(AITalk_TTtsParam param);
        int AITalkAPI_GetStatus(int status, int code);
        int AITalkAPI_TextToSpeech(int jobID, AITalk_TJobParam param, String text);
        int AITalkAPI_CloseSpeech(int jobID, int useEvent);
        //TODO: short[] に合う型を見つける
        int AITalkAPI_GetData(int jobID, short[] rawBuf, int lenBuf, int size);
        int AITalkAPI_TextToKana(int jobID, AITalk_TJobParam param, String text);
        int AITalkAPI_CloseKana(int jobID, int useEvent);
        //TODO: StringBuilderに合う型を見つける
        //int AITalkAPI_GetKana(todo: StringBuilder?)
        int AITalkAPI_GetJeitaControl(int jobID, String ctrl);
        int AITalkAPI_BLoadWordDic();
        int AITalkAPI_ReloadWordDic(String pathDic);
        int AITalkAPI_ReloadPhraseDic(String pathDic);
        int AITalkAPI_ReloadSymbolDic(String pathDic);
        //TODO: StringBuilderに合う型を見つける
        //int AITalkAPI_VersionInfo(int a1, StringBuilder todo: StringBuilder?,)

        @Structure.FieldOrder({"hzVoiceDB", "dirVoiceDBS", "msecTimeout", "pathLicense", "codeAuthSeed", "__reserved__"})
        class AITalk_TConfig extends Structure {
            public int hzVoiceDB;
            public String dirVoiceDBS;
            public int msecTimeout;
            public String pathLicense;
            public String codeAuthSeed;
            public int __reserved__;

            public AITalk_TConfig() {
                super(1);
            }
        }

        int MAX_VOICE_NAME = 80;

        @Structure.FieldOrder({"size", "procTextBuf", "procRawBuf", "procEventTts", "lenTextBufBytes", "lenRawBufBytes", "volume", "pauseBegin", "pauseTerm"
                , "extendFormat", "voiceName", "jeita", "numSpeakers", "__reserved__", "speaker"})
        class AITalk_TTtsParam extends Structure {
            public int size;
            public AITalkProcTextBuf procTextBuf;
            public AITalkProcRawBuf procRawBuf;
            public AITalkProcEventTTS procEventTts;
            public int lenTextBufBytes;
            public int lenRawBufBytes;
            public float volume;
            public int pauseBegin;
            public int pauseTerm;
            //@uses ExtendFormat
            public int extendFormat;
            public String voiceName;
            public TJeitaParam jeita = new TJeitaParam();
            public int numSpeakers;
            public int __reserved__;
            public TSpeakerParam[] speaker;

            public AITalk_TTtsParam(int len) {
                super(1);

                speaker = (TSpeakerParam[]) new TSpeakerParam().toArray(len);
            }
        }

        @Structure.FieldOrder({"femaleName", "maleName", "pauseMiddle", "pauseLong", "pauseSentence", "control"})
        class TJeitaParam extends Structure {
            public String femaleName;
            public String maleName;
            public int pauseMiddle;
            public int pauseLong;
            public int pauseSentence;
            public String control;

            public TJeitaParam() {
                super(1);
            }
        }

        @Structure.FieldOrder({"voiceName", "volume", "speed", "range", "pauseMiddle", "pauseLong", "styleRate"})
        class TSpeakerParam extends Structure {
            public String voiceName;
            public float volume;
            public float speed;
            public float range;
            public int pauseMiddle;
            public int pauseLong;
            public String styleRate;

            public TSpeakerParam() {
                super(1);
            }
        }

        /*
        @Structure.FieldOrder({"size", "procTextBuf", "procRawBuf", "procEventTts", "lenTextBufBytes", "lenRawBufBytes", "volume", "pauseBegin", "pauseTerm"
            , "extendFormat", "voiceName", "jeita", "numSpeakers", "__reserved__", "speaker"})
        class AITalk_TTtsParam extends Structure {
            public static final int MAX_VOICENAME_ = 80;
            public int size;
            public AITalkProcTextBuf procTextBuf;
            public AITalkProcRawBuf procRawBuf;
            public AITalkProcEventTTS procEventTts;
            public int lenTextBufBytes;
            public int lenRawBufBytes;
            public float volume;
            public int pauseBegin;
            public int pauseTerm;
            //@uses ExtendFormat
            public int extendFormat = ExtendFormat.NONE;
            public String voiceName;
            public TJeitaParam jeita;
            public int numSpeakers;
            public int __reserved__;
            //public Pointer speaker;
            public TSpeakerParam[] speaker = (TSpeakerParam[]) new TSpeakerParam().toArray(512);

            public AITalk_TTtsParam() {
                super(1);

                //getPointer().setMemory(512, 512, (byte) 512);
            }
        }

        @Structure.FieldOrder({"femaleName", "maleName", "pauseMiddle", "pauseLong", "pauseSentence", "control"})
        class TJeitaParam extends Structure {
            public String femaleName;
            public String maleName;
            public int pauseMiddle;
            public int pauseLong;
            public int pauseSentence;
            public String control;

            public TJeitaParam() {
                //super(1);
            }
        }

        @Structure.FieldOrder({"voiceName", "volume", "speed", "pitch", "range", "pauseMiddle", "pauseLong", "pauseSentence", "styleRate"})
        class TSpeakerParam extends Structure {
            public String voiceName;
            public float volume;
            public float speed;
            public float pitch;
            public float range;
            public int pauseMiddle;
            public int pauseLong;
            public int pauseSentence;
            public String styleRate;

            public TSpeakerParam() {
                //super(1);
            }
        }*/

        @Structure.FieldOrder({"modeInOut", "userData"})
        class AITalk_TJobParam extends Structure {
            public int modeInOut;
            public int userData;
        }
    }

    public static class ExtendFormat {
        public static short NONE = 0;
        public static short JEITA_RUBY = 1;
        public static short AUTO_BOOK_MARK = 16;
    }

    public static class AIAudioFormatType {
        public static short AIAUIDOTYPE_NONE = 0;
        public static short AIAUDIOTYPE_PCM_16 = 1;
        public static short AIAUDIOTYPE_PCM_8 = 769;
        public static short AIAUDIOTYPE_MULAW_8 = 7;
        public static short AIAUDIOTYPE_ALAW_8 = 6;
    }

    public static class AITalkJobInOut {
        public static short AITALKIOMODE_PLAIN_TO_WAVE = 11;
        public static short AITALKIOMODE_AIKANA_TO_WAVE = 12;
        public static short AITALKIOMODE_JEITA_TO_WAVE = 13;
        public static short AITALKIOMODE_PLAIN_TO_AIKANA = 21;
        public static short AITALKIOMODE_AIKANA_TO_JEITA = 32;
    }

    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        int SetCurrentDirectoryW(char[] pathName);
    }

    public interface AITalkProcTextBuf extends StdCallLibrary.StdCallCallback {
        int callback(int reasonCode, int jobID, int userData);
    }

    public interface AITalkProcRawBuf extends StdCallLibrary.StdCallCallback {
        int callback(int reasonCode, int jobID, long tick, int userData);
    }

    public interface AITalkProcEventTTS extends StdCallLibrary.StdCallCallback {
        int callback(int reasonCode, int jobID, long tick, String name, int userData);
    }

    public static class AITalkResultCode {
        public static short AITALKERR_USERDIC_NOENTRY = -1012;
        public static short AITALKERR_USERDIC_LOCKED = -1011;
        public static short AITALKERR_COUNT_LIMIT = -1004;
        public static short AITALKERR_READ_FAULT = -1003;
        public static short AITALKERR_PATH_NOT_FOUND = -1002;
        public static short AITALKERR_FILE_NOT_FOUND = -1001;
        public static short AITALKERR_OUT_OF_MEMORY = -206;
        public static short AITALKERR_JOB_BUSY = -203;
        public static short AITALKERR_INVALID_JOBID = -202;
        public static short AITALKERR_TOO_MANY_JOBS = -201;
        public static short AITALKERR_LICENSE_EXPIRED = -101;
        public static short AITALKERR_LICENSE_ABSENT = -100;
        public static short AITALKERR_INSUFFICIENT = -20;
        public static short AITALKERR_NOT_LOADED = -11;
        public static short AITALKERR_NOT_INITIALIZED = -10;
        public static short AITALKERR_WAIT_TIMEOUT = -4;
        public static short AITALKERR_INVALID_ARGUMENT = -3;
        public static short AITALKERR_UNSUPPORTED = -2;
        public static short AITALKERR_INTERNAL_ERROR = -1;
        public static short AITALKERR_SUCCESS = 0;
        public static short AITALKERR_ALREADY_INITIALIZED = 10;
        public static short AITALKERR_ALREADY_LOADED = 11;
        public static short AITALKERR_PARTIALLY_REGISTERED = 21;
        public static short AITALKERR_NOMORE_DATA = 204;
    }


    private static final VoiceRoid2 INSTANCE = VoiceRoid2.getInstance();

    public void init() {
        VoiceRoid2.AITalk_TConfig config = new VoiceRoid2.AITalk_TConfig();

        config.hzVoiceDB = 44100;
        config.msecTimeout = 10000;
        config.pathLicense = path_64 + "\\aitalk.lic";
        config.dirVoiceDBS = path_32 + "\\Voice";
        config.codeAuthSeed = "ORXJC6AIWAUKDpDbH2al";
        config.__reserved__ = 0;

        checkError(INSTANCE.AITalkAPI_Init(config), 0);
    }

    public void loadLang(String lang) {
        Kernel32 kernel32 = Kernel32.INSTANCE;

        try {
            checkError(INSTANCE.AITalkAPI_LangClear(), 0, -11);

            if (kernel32.SetCurrentDirectoryW(Native.toCharArray(path_64)) == 0) {
                throw new RuntimeException();
            }

            checkError(INSTANCE.AITalkAPI_LangLoad(path_32 + "\\Lang\\" + lang), 0);
        } finally {
            kernel32.SetCurrentDirectoryW(Native.toCharArray(getWorkingDirectory()));
        }
    }

    public List<String> getAllLang() {
        return getAllDirectoryName(new File(path_32 + "\\Lang\\"));
    }

    //Base64.getDecoder().decode(Base64.getEncoder().encodeToString());
    public void loadVoice(String voice) {
        checkError(INSTANCE.AITalkAPI_VoiceLoad(voice), 0);

        IntByReference reference = new IntByReference();
        checkError(INSTANCE.AITalkAPI_GetParam((VoiceRoid2.AITalk_TTtsParam) null, reference), -20);
    }

    public List<String> getAllVoice() {
        return getAllDirectoryName(new File(path_32 + "\\Voice\\"));
    }

    public List<String> getAllDirectoryName(File directory) {
        List<String> name = new ArrayList<>();

        if (directory != null && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    name.add(FilenameUtils.getBaseName(file.getName()));
                }
            }
        }

        return name;
    }

    public String getWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public static void main(String[] args) {

        Testing test = new Testing();
        test.init();

        System.out.println(test.getAllVoice());
        System.out.println(test.getAllLang());

        test.loadLang("standard");
        test.loadVoice("akari_44");

        VoiceRoid2.AITalk_TJobParam param = new VoiceRoid2.AITalk_TJobParam();

        VoiceRoid2.AITalk_TTtsParam param2 = new VoiceRoid2.AITalk_TTtsParam(1);

        System.out.println(param2.size());

        //System.out.println(param.size());

        param.modeInOut = AITalkJobInOut.AITALKIOMODE_PLAIN_TO_AIKANA;
        int result = 0;


        /*VoiceRoid2.TSpeakerParam tSpeakerParam = new VoiceRoid2.TSpeakerParam();
        tSpeakerParam.pauseLong = 370;
        tSpeakerParam.pauseSentence = 800;
        tSpeakerParam.pauseMiddle = 150;
        tSpeakerParam.volume = 1.0f;
        tSpeakerParam.speed = 1.0f;
        tSpeakerParam.pitch = 1.0f;
        tSpeakerParam.range = 1.0f;

        tSpeakerParam.styleRate = "11";
        tSpeakerParam.voiceName = "akari_44";*/

        //VoiceRoid2.AITalk_TTtsParam ttsParam = new VoiceRoid2.AITalk_TTtsParam();
        //ttsParam.Speaker = tSpeakerParam.toArray(tSpeakerParam.size() / 4);

        //debugs
        /*result = INSTANCE.AITalkAPI_VoiceLoad("akari_44");

        if (checkError(result, AITalkResultCode.AITALKERR_SUCCESS)) {
            IntByReference reference = new IntByReference(0);

            result = INSTANCE.AITalkAPI_GetParam((VoiceRoid2.AITalk_TTtsParam) null, reference);

            String dir = System.getProperty("user.dir");

            //System.out.println(path_32 + "\\Lang\\standard");

            //C.INSTANCE.chmod(path_32, 755);
            try {
                result = INSTANCE.AITalkAPI_LangLoad(path_32 + "\\Lang\\standard");
            } finally {


                System.setProperty("user.dir", dir);
            }

            if (checkError(result, AITalkResultCode.AITALKERR_INSUFFICIENT)) {

                VoiceRoid2.AITalk_TTtsParam talk_tTtsParam = new VoiceRoid2.AITalk_TTtsParam();
                talk_tTtsParam.size = reference.getValue();
                talk_tTtsParam.speaker = new Memory(512);

                Pointer pointer = new Memory(512);

                result = INSTANCE.AITalkAPI_GetParam(pointer, reference);

                System.out.println(result);

            }
        }*/
    }

    public static boolean checkError(final int code, final int... allowed) {
        if (Arrays.stream(allowed).anyMatch(a -> code == a)) {
            return true;
        }

        return checkError(code) == 0;
    }

    public static int checkError(final int code) {
        if (code == 0) return code;

        Field[] fields = AITalkResultCode.class.getFields();

        for (Field field : fields) {
            try {
                final int value = field.getInt(null);

                final String name = field.getName();

                if (value == code) {
                    throw new IllegalStateException(name + " (" + value + ")");
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        throw new IllegalStateException("INVALID_CODE (" + code + ")");
    }
}
