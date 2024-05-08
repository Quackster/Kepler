UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_food', `interactor`='pet_food' WHERE  `sprite` IN ('petfood1','petfood2','petfood3');
UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_cat_food', `interactor`='pet_food' WHERE  `sprite` IN ('goodie2');
UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_dog_food', `interactor`='pet_food' WHERE  `sprite` IN ('goodie1');
UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_croc_food', `interactor`='pet_food' WHERE  `sprite` IN ('petfood4');

UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_water_bowl', `interactor`='pet_water_bowl' WHERE  `sprite` IN ('waterbowl*1','waterbowl*2','waterbowl*3','waterbowl*4', 'waterbowl*5');
UPDATE `items_definitions` SET `behaviour`='can_stand_on_top,requires_rights_for_interaction,pet_toy', `interactor`='pet_toy' WHERE  `sprite` IN ('toy1', 'toy1*1', 'toy1*2', 'toy1*3', 'toy1*4');