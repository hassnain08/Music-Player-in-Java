import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

public class MusicPlayer {
    private Clip clip;
    private AudioInputStream audioInputStream;
    private long clipTimePosition = 0;//used to indicate the current position of the song.

    public void play(String filePath){
        try{
            File soundFile = new File(filePath);
            if(!soundFile.exists())
            {
                throw new IllegalArgumentException("File not found: " + filePath);
            }

            if(clip == null)
            {
                audioInputStream = AudioSystem.getAudioInputStream(soundFile.getAbsoluteFile());
                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
            }

            
            if(clip.isRunning())
            {
                clip.stop();
            }

            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
        }
        catch(UnsupportedAudioFileException|IOException |LineUnavailableException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error playing the song: " + e.getMessage());
        }
        catch(IllegalArgumentException e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public void stop()//function to stop the current playing song
    {
        if (clip != null && clip.isOpen())
        {
            clip.stop();
            clip.close();
            clipTimePosition = 0;//reset time position  
            clip = null;//reset clip
        }
    }

    public void pause()//function to pause the song when it is playing
    {
        if(clip != null && clip.isRunning())
        {
            clipTimePosition = clip.getMicrosecondPosition();
            clip.stop();
        }
    }

    public void resume()//funtion for resuming the song when paused
    {
        if (clip != null && !clip.isRunning())
        {
            clip.setMicrosecondPosition(clipTimePosition);
            clip.start();
        }
    }

    public void close()
    {
        if (clip != null && clip.isOpen())
        {
            clip.close();
        }
    }
}