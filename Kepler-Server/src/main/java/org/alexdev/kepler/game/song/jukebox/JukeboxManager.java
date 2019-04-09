package org.alexdev.kepler.game.song.jukebox;

import org.alexdev.kepler.dao.mysql.JukeboxDao;
import org.alexdev.kepler.dao.mysql.SongMachineDao;
import org.alexdev.kepler.game.song.Song;

import java.util.HashMap;
import java.util.Map;

public class JukeboxManager {
    private static JukeboxManager instance;

    /**
     * Get the disks for the jukebox.
     *
     * @param soundmachineId the jukebox id
     * @return the list of disks
     */
    public Map<BurnedDisk, Song> getDisks(long soundmachineId) {
        Map<BurnedDisk, Song> jukeboxDisks = new HashMap<>();

        for (BurnedDisk burnedDisk : JukeboxDao.getDisks(soundmachineId)) {
            Song song = SongMachineDao.getSong(burnedDisk.getSongId());

            if (song != null) {
                jukeboxDisks.put(burnedDisk, song);
            }
        }

        return jukeboxDisks;
    }

    /**
     * Get the instance of {@link JukeboxManager}
     *
     * @return the instance
     */
    public static JukeboxManager getInstance() {
        if (instance == null) {
            instance = new JukeboxManager();
        }

        return instance;
    }
}
