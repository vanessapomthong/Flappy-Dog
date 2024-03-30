import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Resource {
	
	public static Clip getSound(String path) {
		File file = new File(path);
		Clip clip = null;
		try {
			clip = AudioSystem.getClip();
			// same situation why i have ClassLoader here
			if(file.exists())
				clip.open(AudioSystem.getAudioInputStream(file));
			else {
				path = path.substring(path.indexOf("/") + 1);
				clip.open(AudioSystem.getAudioInputStream(ClassLoader.getSystemClassLoader().getResource(path)));
			}
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return clip;
	}
	
	public static boolean isJar() {
		Matcher m = Pattern.compile("^file:").matcher(ClassLoader.getSystemClassLoader().getResource("").toString());
		return !m.find();
	}
	
}
