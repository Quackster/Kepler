#include "communication/messages/incoming_message.h"
#include "communication/messages/outgoing_message.h"

void UPDATE_ACCOUNT(entity *player, incoming_message *message) {
    im_read_b64_int(message);
    char *password = im_read_str(message);

    im_read_b64_int(message);
    char *date_of_birth = im_read_str(message);

    im_read_b64_int(message);
    char *new_password = im_read_str(message);

    if (password == NULL || date_of_birth == NULL || new_password == NULL) {
        goto cleanup;
        return;
    }

    outgoing_message *om = om_create(169); // "Bi"
    
    int error_id = 0;

    if (strcmp(password, new_password) != 0) {
        error_id = 1;
    } else {
        
    }

    om_write_int(om, error_id);
    player_send(player, om);
    om_cleanup(om);


    cleanup:
		free(password);
		free(date_of_birth);
		free(new_password);
}
