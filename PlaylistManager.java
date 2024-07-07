import java.io.*;
import java.util.*;

public class PlaylistManager implements Serializable
{
    private Map<String, List<String>> playlists;//creating a map that stores playlist names as keys and list of songs

    public PlaylistManager() {
        playlists= new HashMap<>();
    }

    public boolean createPlaylist(String playlistName)
    {
        if(!playlists.containsKey(playlistName))
        {
            playlists.put(playlistName, new ArrayList<>());
            return true;
        }
        return false;//reutrn false when with same name already exists.
    }

    public boolean addSongToPlaylist(String playlistName, String song)
    {
        List<String> playlist = playlists.get(playlistName);
        if(playlist != null && !playlist.contains(song))
        {
            playlist.add(song);
            return true;
        }
        return false;//return alse when song already exists in playlist or playlist not exists.
    }

    public boolean removeSongFromPlaylist(String playlistName, String song)
    {
        List<String> playlist = playlists.get(playlistName);
        if(playlist != null && playlist.contains(song))
        {
            playlist.remove(song);
            return true;
        }
        return false; // Song not found in playlist or playlist does not exist
    }

    public List<String> getAllPlaylists()
    {
        return new ArrayList<>(playlists.keySet());
    }

    public List<String> getSongsInPlaylist(String playlistName)
    {
        return playlists.getOrDefault(playlistName, new ArrayList<>());
    }

    public boolean playlistExists(String playlistName)
    {
        return playlists.containsKey(playlistName);
    }

    public boolean deletePlaylist(String playlistName)
    {
        if (playlists.containsKey(playlistName))
        {
            playlists.remove(playlistName);
            return true;//return true when playlist exists.
        }
        return false;//return false when playlist does not exist
    }

    public void savePlaylists()throws IOException
    {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("playlists.dat")))
        {
            oos.writeObject(playlists);
        }
    }

    public void loadPlaylists() throws IOException, ClassNotFoundException
    {
        File file = new File("playlists.dat");
        if(file.exists())
        {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file)))
            {
                Object obj = ois.readObject();
                if(obj instanceof Map<?, ?>)
                {
                    @SuppressWarnings("unchecked")
                    Map<String, List<String>> loadedPlaylists = (Map<String, List<String>>) obj;
                    playlists = loadedPlaylists;
                }
                else
                {
                    throw new IOException("Unexpected data type in playlists.dat");
                }
            }
        }
    }
}