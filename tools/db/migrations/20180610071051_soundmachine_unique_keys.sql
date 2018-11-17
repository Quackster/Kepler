-- migrate:up
ALTER TABLE `soundmachine_songs`
  ADD UNIQUE KEY `id` (`id`);

ALTER TABLE `soundmachine_playlists`
  ADD KEY `machineid` (`item_id`),
  ADD KEY `songid` (`song_id`);
  
ALTER TABLE `soundmachine_songs`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- migrate:down

