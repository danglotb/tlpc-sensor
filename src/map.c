#include "map.h"

int
map_contains(const char *identifier) {
    const int hash = map_get_hash(identifier);
    return strcmp(map[hash].identifier, identifier);
}

struct group_leaders_fd 
map_get(const char *identifier) {
    const int hash = map_get_hash(identifier);
    return map[hash].group_leaders;
}

int
map_put(const char *identifier,  int rapl_group_leader_fd, int perf_group_leader_fd) {
    if (map_contains(identifier)) {
        printf("The map contains already an association for the key %s\n", identifier);
        return 1;
    }
    const int hash = map_get_hash(identifier);
    strcpy(map[hash].identifier, identifier);
    map[hash].group_leaders.perf_group_leader_fd = perf_group_leader_fd;
    map[hash].group_leaders.rapl_group_leader_fd = rapl_group_leader_fd;
    return 0;
}

int
map_remove(const char *identifier) {
    if (!map_contains(identifier)) {
        printf("The map does not contain an association for the key %s\n", identifier);
        return 1;
    }
    const int hash = map_get_hash(identifier);
    map[hash].identifier = "";
    return 0;
}

int 
map_get_hash(const char *identifier) {
    unsigned long hash = 5381;
    int c;
    while ((c = *identifier++)) {
        hash = ((hash << 5) + hash) + c;
    }
    return hash;
}