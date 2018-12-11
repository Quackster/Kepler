-- migrate:up
ALTER TABLE `users_badges`
  ADD CONSTRAINT `users_badges_users_FK` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

-- migrate:down

