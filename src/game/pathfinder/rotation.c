int calculate_human_direction(int x1, int y1, int X2, int Y2) {
    int rotation = 0;

    if (x1 > X2 && y1 > Y2)
        rotation = 7;
    else if (x1 < X2 && y1 < Y2)
        rotation = 3;
    else if (x1 > X2 && y1 < Y2)
        rotation = 5;
    else if (x1 < X2 && y1 > Y2)
        rotation = 1;
    else if (x1 > X2)
        rotation = 6;
    else if (x1 < X2)
        rotation = 2;
    else if (y1 < Y2)
        rotation = 4;
    else if (y1 > Y2)
        rotation = 0;

    return rotation;
}

int calculate_walk_direction(int x, int y, int to_x, int to_y) {
    if (x == to_x) {
        if (y < to_y)
            return 4;
        else
            return 0;
    } else if (x > to_x) {
        if (y == to_y)
            return 6;
        else if (y < to_y)
            return 5;
        else
            return 7;
    } else if (x < to_x) {
        if (y == to_y)
            return 2;
        else if (y < to_y)
            return 3;
        else
            return 1;
    }

    return 0;
}