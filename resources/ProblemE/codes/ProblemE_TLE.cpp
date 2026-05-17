#include <iostream>
#include <vector>
#include <string>
#include <algorithm>

using namespace std;

int V, E;
vector<int> adj[1000005];
int label[1000005];

bool is_matching_cut() {
    int v0_count = 0, v1_count = 0;
    for (int i = 1; i <= V; i++) {
        if (label[i] == 0) v0_count++;
        else v1_count++;
    }
    if (v0_count == 0 || v1_count == 0) return false;

    vector<int> opposite_neighbors(V + 1, 0);
    for (int u = 1; u <= V; u++) {
        for (int v : adj[u]) {
            if (label[u] != label[v]) {
                opposite_neighbors[u]++;
                if (opposite_neighbors[u] > 1) return false;
            }
        }
    }
    return true;
}

int main() {
    cin >> V >> E;
    for (int i = 0; i < E; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }
    for (int i = 1; i < (1 << min(V, 20)); i++) {
        for (int j = 0; j < V; j++) {
            label[j + 1] = (i >> j) & 1;
        }
        if (is_matching_cut()) {
            for (int j = 1; j <= V; j++) cout << label[j];
            cout << endl;
            return 0;
        }
    }
    cout << -1 << endl;
    return 0;
}