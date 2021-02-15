package de.dgs.apps.openspeedyfx.engine.audio;

public class ManualAudioPlayerTest {
    public static void main(String[] args) throws Exception {
        System.out.println("Start of manual test for " + AudioPlayer.class.getSimpleName() + ":");
        System.out.println("------------------------------------------");

        final String musicPath = "/assets/resourcepack/default/music/";
        final String firstAudioPath = musicPath + "storyMainTheme.ogg";
        final String secondAudioPath = musicPath + "menuLoop.wav";

        byte[] audioBytes = AudioPlayer.class.getResourceAsStream(firstAudioPath).readAllBytes();
        AudioPlayer simpleAudioPlayer = new AudioPlayer(audioBytes, true);

        simpleAudioPlayer.play();
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(4000);

        simpleAudioPlayer.pause();
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(1000);

        simpleAudioPlayer.resume();
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(2000);

        simpleAudioPlayer.pause();
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(1000);

        simpleAudioPlayer.jump(0);
        System.out.println("JUMP");
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(5000);

        simpleAudioPlayer.restart();
        System.out.println("RESTART");
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(4000);

        simpleAudioPlayer.close();
        System.out.println(simpleAudioPlayer.getPlayerState());

        simpleAudioPlayer.reloadAudio();
        System.out.println("RELOAD");
        Thread.sleep(1000);

        simpleAudioPlayer.play();
        System.out.println(simpleAudioPlayer.getPlayerState());
        Thread.sleep(3000);

        simpleAudioPlayer.close();
        System.out.println(simpleAudioPlayer.getPlayerState());

        audioBytes = AudioPlayer.class.getResourceAsStream(secondAudioPath).readAllBytes();
        simpleAudioPlayer.reloadAudio(audioBytes);
        System.out.println("RELOAD (new audio, loop-test)");
        Thread.sleep(1000);

        simpleAudioPlayer.play();
        System.out.println(simpleAudioPlayer.getPlayerState());

        Thread.sleep(1000 * 15);

        simpleAudioPlayer.stop();
        System.out.println(simpleAudioPlayer.getPlayerState());
    }
}
