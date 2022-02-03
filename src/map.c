#include "map.h"

int
map_contains(const char *identifier) {
    const int hash = map_get_hash(identifier);
    return map[hash].identifier != NULL;
}

struct value 
map_get(const char *identifier) {
    const int hash = map_get_hash(identifier);
    return map[hash].group_leaders;
}

int
map_put(const char *identifier,  int rapl_group_leader_fd, int perf_group_leader_fd) {
    const int hash = map_get_hash(identifier);
    if (map[hash].identifier != NULL && !strcmp(map[hash].identifier, identifier)) {
        printf("Collision detected between %s and %s (hash: %d)", identifier, map[hash].identifier, hash);
        exit(EXIT_FAILURE);
    }
    map[hash].identifier = (char*) malloc(strlen(identifier) * sizeof(char));
    strcpy(map[hash].identifier, identifier);
    map[hash].group_leaders.perf_group_leader_fd = perf_group_leader_fd;
    map[hash].group_leaders.rapl_group_leader_fd = rapl_group_leader_fd;
    map[hash].group_leaders.starting_time = clock();
    return 0;
}

int
map_remove(const char *identifier) {
    if (!map_contains(identifier)) {
        printf("The map does not contain an association for the key %s\n", identifier);
        return 1;
    }
    const int hash = map_get_hash(identifier);
    map[hash].identifier = NULL;
    return 0;
}

int 
map_get_hash(const char *identifier) {
    unsigned long hash = 5381;
    int c;
    while ((c = *identifier++)) {
        hash = ((hash << 5) + hash) + c;
    }
    return hash % SIZE_OF_MAP;
}