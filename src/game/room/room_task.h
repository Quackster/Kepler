typedef struct runnable_s runnable;
typedef struct room_s room;
typedef struct room_user_s room_user;

void room_start_tasks(room *room);
void room_stop_tasks(room *room);