import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SpotifyCloneUI
{
    private JFrame frame;
    private Login_SignUp userAuth;
    private MusicPlayer musicPlayer;
    private PlaylistManager playlistManager;
    private DefaultListModel<String> songListModel;
    private JList<String> songList;
    private JButton addToPlaylistButton;

    public SpotifyCloneUI() {
        userAuth = new Login_SignUp();
        musicPlayer = new MusicPlayer();
        playlistManager = new PlaylistManager();

        try
        {
            playlistManager.loadPlaylists();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        initializeLoginGUI();
    }

    private void initializeLoginGUI() {
        frame = new JFrame("Spotify Clone");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.getContentPane().setBackground(new Color(30, 30, 30));

        JPanel loginPanel = createLoginPanel();
        frame.add(loginPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private void initializeMainGUI() {
        frame.setTitle("Spotify 2.0");
        frame.setSize(800, 600);
        frame.getContentPane().removeAll(); // Clear login panel
        frame.setLayout(new BorderLayout());

        JMenuBar menuBar = createMenuBar();
        menuBar.setBackground(new Color(30, 30, 30)); // Set background color of menu bar
        menuBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.setJMenuBar(menuBar);

        

        songListModel = new DefaultListModel<>();
        songList = new JList<>(songListModel);
        loadSongs();
        songList.setBackground(new Color(50, 50, 50));
        songList.setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(songList);
        scrollPane.getViewport().setBackground(new Color(50, 50, 50));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel();
        controlPanel.setBackground(new Color(30, 30, 30));
        frame.add(controlPanel, BorderLayout.SOUTH);

        

        frame.revalidate();
        frame.repaint();
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
    
        JMenu menu = new JMenu("Menu");
        menu.setForeground(Color.WHITE);
    
        JMenuItem viewSongsMenuItem = new JMenuItem("View All Songs");
        JMenuItem createPlaylistMenuItem = new JMenuItem("Create Playlist");
        JMenuItem viewPlaylistsMenuItem = new JMenuItem("View Playlists");
    
        viewSongsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "All Songs:\n" + getAllSongs());
            }
        });
    
        createPlaylistMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playlistName = JOptionPane.showInputDialog(frame, "Enter playlist name:");
                if (playlistName != null && !playlistName.isEmpty()) {
                    if (!playlistManager.playlistExists(playlistName)) {
                        playlistManager.createPlaylist(playlistName);
                        try {
                            playlistManager.savePlaylists();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        JOptionPane.showMessageDialog(frame, "Playlist '" + playlistName + "' created successfully.");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Playlist '" + playlistName + "' already exists.");
                    }
                }
            }
        });
    
        viewPlaylistsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPlaylistDialog();
            }
        });
    
        JMenuItem addToPlaylistMenuItem = new JMenuItem("Add to Playlist");
        addToPlaylistMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToPlaylistAction();
            }
        });
    
        JMenuItem deletePlaylistMenuItem = new JMenuItem("Delete Playlist");
        deletePlaylistMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePlaylistAction();
            }
        });
    
        // Add new menu item for adding a song
        JMenuItem addSongMenuItem = new JMenuItem("Add Song");
        addSongMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddSongInstructions();
            }
        });
    
        menu.add(addToPlaylistMenuItem);
        menu.add(viewSongsMenuItem);
        menu.add(createPlaylistMenuItem);
        menu.add(viewPlaylistsMenuItem);
        menu.add(deletePlaylistMenuItem);
        menu.add(addSongMenuItem); // Add the new menu item for adding a song
    
        menuBar.add(menu);
    
        return menuBar;
    }

    private void showAddSongInstructions() {
        String instructions = "To add a song:\n\n" +
                "1. Convert the audio file of the song to WAV format.\n" +
                "2. Copy the WAV file to the music directory of the project.\n" +
                "3. Restart the application to fetch and display the newly added song.";
    
        JOptionPane.showMessageDialog(frame, instructions, "Add Song Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    
    

    private void deletePlaylistAction() {
        String[] playlistsArray = getAllPlaylistsArray();
        String selectedPlaylist = (String) JOptionPane.showInputDialog(frame,
                "Select Playlist to delete:", "Delete Playlist",
                JOptionPane.PLAIN_MESSAGE, null, playlistsArray, null);
    
        if (selectedPlaylist != null) {
            int option = JOptionPane.showConfirmDialog(frame,
                    "Are you sure you want to delete playlist '" + selectedPlaylist + "'?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
    
            if (option == JOptionPane.YES_OPTION) {
                if (playlistManager.deletePlaylist(selectedPlaylist)) {
                    try {
                        playlistManager.savePlaylists();
                        JOptionPane.showMessageDialog(frame, "Playlist '" + selectedPlaylist + "' deleted successfully.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: Unable to save playlists.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Playlist '" + selectedPlaylist + "' not found.");
                }
            }
        }
    }
    
    
    private String[] getAllPlaylistsArray() {
        List<String> playlists = playlistManager.getAllPlaylists();
        return playlists.toArray(new String[0]);
    }

    
    private void loadSongs() {
        String musicDirectoryPath = "E:/Internship/Java/Java Project"; // Directory containing the music files

        File musicDirectory = new File(musicDirectoryPath);

        // Check if the directory exists and is a directory
        if (musicDirectory.exists() && musicDirectory.isDirectory()) {
            File[] files = musicDirectory.listFiles((dir, name) -> name.toLowerCase().endsWith(".wav"));

            if (files != null && files.length > 0) {
                for (File file : files) {
                    songListModel.addElement(file.getName());
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No .wav files found in the directory.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Music directory does not exist or is not a directory.");
        }
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(new Color(30, 30, 30));
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("Spotify 2.0");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, constraints);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(usernameLabel, constraints);

        JTextField usernameField = new JTextField(15);
        constraints.gridx = 1;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(passwordLabel, constraints);

        JPasswordField passwordField = new JPasswordField(15);
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(30, 215, 96));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, constraints);

        JButton signupButton = new JButton("Signup");
        signupButton.setBackground(new Color(255, 69, 58));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.CENTER;
        panel.add(signupButton, constraints);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (userAuth.login(usernameField.getText(), new String(passwordField.getPassword()))) {
                        JOptionPane.showMessageDialog(frame, "Login successful!");
                        initializeMainGUI();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Login failed!");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: Unable to login.");
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    userAuth.signup(usernameField.getText(), new String(passwordField.getPassword()));
                    JOptionPane.showMessageDialog(frame, "Signup successful!");
                    initializeMainGUI();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Error: Unable to signup.");
                }
            }
        });

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 5));

        JButton playButton = new JButton("Play");
        playButton.setBackground(new Color(30, 215, 96));
        playButton.setFocusPainted(false);

        JButton pauseButton = new JButton("Pause");
        pauseButton.setBackground(new Color(255, 215, 0));
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setFocusPainted(false);

        JButton stopButton = new JButton("Stop");
        stopButton.setBackground(new Color(255, 69, 58));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);

        addToPlaylistButton = new JButton("Add to Playlist"); // Initialize addToPlaylistButton
        addToPlaylistButton.setBackground(new Color(0, 123, 255));
        addToPlaylistButton.setForeground(Color.WHITE);
        addToPlaylistButton.setFocusPainted(false);
        panel.add(addToPlaylistButton);

        JButton removeFromPlaylistButton = new JButton("Remove from Playlist");
        removeFromPlaylistButton.setBackground(new Color(255, 0, 123));
        removeFromPlaylistButton.setForeground(Color.WHITE);
        removeFromPlaylistButton.setFocusPainted(false);
        panel.add(removeFromPlaylistButton);

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playAction();
            }
        });

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.pause();
            }
        });

        stopButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.stop();
            }
        });

        addToPlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addToPlaylistAction();
            }
        });

        removeFromPlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeFromPlaylistAction();
            }
        });

        panel.add(playButton);
        panel.add(pauseButton);
        panel.add(stopButton);

        return panel;
    }

    private void playAction() {
        String selectedSong = songList.getSelectedValue();
        if (selectedSong != null) {
            String musicDirectoryPath = "E:/Internship/Java/Java Project"; // Directory containing the music files
            String filePath = musicDirectoryPath + "/" + selectedSong;
            musicPlayer.play(filePath);
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a song to play.");
        }
    }

    private void addToPlaylistAction() {
        String selectedSong = songList.getSelectedValue();
        if (selectedSong != null) {
            String[] playlistsArray = getAllPlaylistsArray();
            String selectedPlaylist = (String) JOptionPane.showInputDialog(frame,
                    "Select Playlist to add song:", "Add to Playlist",
                    JOptionPane.PLAIN_MESSAGE, null, playlistsArray, null);

            if (selectedPlaylist != null) {
                if (playlistManager.addSongToPlaylist(selectedPlaylist, selectedSong)) {
                    try {
                        playlistManager.savePlaylists();
                        JOptionPane.showMessageDialog(frame, "Song added to playlist '" + selectedPlaylist + "' successfully.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: Unable to save playlists.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Song already exists in playlist or playlist does not exist.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a song to add to a playlist.");
        }
    }

    private void removeFromPlaylistAction() {
        String[] playlistsArray = getAllPlaylistsArray();
        String selectedPlaylist = (String) JOptionPane.showInputDialog(frame,
                "Select Playlist to remove song:", "Remove from Playlist",
                JOptionPane.PLAIN_MESSAGE, null, playlistsArray, null);

        if (selectedPlaylist != null) {
            List<String> playlistSongs = playlistManager.getSongsInPlaylist(selectedPlaylist);
            String[] playlistSongsArray = playlistSongs.toArray(new String[0]);

            String selectedSong = (String) JOptionPane.showInputDialog(frame,
                    "Select Song to remove:", "Remove from Playlist",
                    JOptionPane.PLAIN_MESSAGE, null, playlistSongsArray, null);

            if (selectedSong != null) {
                if (playlistManager.removeSongFromPlaylist(selectedPlaylist, selectedSong)) {
                    try {
                        playlistManager.savePlaylists();
                        JOptionPane.showMessageDialog(frame, "Song removed from playlist '" + selectedPlaylist + "' successfully.");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, "Error: Unable to save playlists.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Song not found in playlist or playlist does not exist.");
                }
            }
        }
    }

    private void showPlaylistDialog() {
        List<String> playlists = playlistManager.getAllPlaylists();
    
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));
        textArea.setBackground(new Color(240, 240, 240));
    
        StringBuilder sb = new StringBuilder();
        sb.append("Playlists:\n\n");
    
        for (String playlist : playlists) {
            sb.append("- ").append(playlist).append("\n");
            List<String> songs = playlistManager.getSongsInPlaylist(playlist);
            for (String song : songs) {
                sb.append("  - ").append(song).append("\n");
            }
            sb.append("\n");
        }
    
        textArea.setText(sb.toString());
    
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
    
        JOptionPane.showMessageDialog(frame, scrollPane, "View Playlists", JOptionPane.PLAIN_MESSAGE);
    }
    
    private String getAllSongs() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < songListModel.getSize(); i++) {
            sb.append(i + 1).append(". ").append(songListModel.getElementAt(i)).append("\n");
        }
        return sb.toString();
    }

    }