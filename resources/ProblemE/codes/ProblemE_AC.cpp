#include <iostream>
#include <vector>
#include <string>
#include <algorithm>
#include <queue>

using namespace std;

int V, E;
vector<int> adj[1000005];
int visited_x[1000005];
int timer_x = 0;

bool is_x(int x) {
    timer_x++;
    int count = 1;
    visited_x[x] = timer_x;
    for (int y : adj[x]) {
        if (visited_x[y] != timer_x) {
            visited_x[y] = timer_x;
            count++;
        }
    }
    for (int y : adj[x]) {
        for (int z : adj[y]) {
            if (visited_x[z] != timer_x) {
                visited_x[z] = timer_x;
                count++;
            }
        }
    }
    return count == V;
}

int label[1000005];
int match_count[1000005];

bool check(const vector<int>& v1) {
    if (v1.empty() || v1.size() == V) return false;
    for (int i = 1; i <= V; i++) label[i] = 0;
    for (int i : v1) label[i] = 1;
    for (int i = 1; i <= V; i++) match_count[i] = 0;
    for (int u = 1; u <= V; u++) {
        for (int v : adj[u]) {
            if (label[u] != label[v]) {
                match_count[u]++;
                if (match_count[u] > 1) return false;
            }
        }
    }
    return true;
}

void print_v1(const vector<int>& v1) {
    string res(V, '0');
    for (int i = 1; i <= V; i++) label[i] = 0;
    for (int i : v1) label[i] = 1;
    for (int i = 1; i <= V; i++) res[i-1] = (label[i] ? '1' : '0');
    cout << res << endl;
}

int main() {
    ios::sync_with_stdio(false);
    cin.tie(NULL);
    if (!(cin >> V >> E)) return 0;
    for (int i = 0; i < E; i++) {
        int u, v; cin >> u >> v;
        adj[u].push_back(v);
        adj[v].push_back(u);
    }

    int x = -1;
    for (int i = 1; i <= V; i++) {
        if (is_x(i)) {
            x = i;
            break;
        }
    }

    if (x == -1) {
        cout << -1 << endl;
        return 0;
    }

    vector<int> N;
    vector<int> N2;
    vector<int> dist(V + 1, -1);
    queue<int> q;
    dist[x] = 0;
    q.push(x);
    while (!q.empty()) {
        int u = q.front(); q.pop();
        if (dist[u] == 1) N.push_back(u);
        if (dist[u] == 2) N2.push_back(u);
        if (dist[u] < 2) {
            for (int v : adj[u]) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    q.push(v);
                }
            }
        }
    }

    vector<int> comp_id(V + 1, 0);
    int cid = 0;
    vector<vector<int>> components;
    vector<int> in_N2(V + 1, 0);
    for (int u : N2) in_N2[u] = 1;
    for (int u : N2) {
        if (comp_id[u] == 0) {
            cid++;
            vector<int> comp;
            queue<int> q2;
            comp_id[u] = cid;
            q2.push(u);
            while (!q2.empty()) {
                int curr = q2.front(); q2.pop();
                comp.push_back(curr);
                for (int v : adj[curr]) {
                    if (in_N2[v] && comp_id[v] == 0) {
                        comp_id[v] = cid;
                        q2.push(v);
                    }
                }
            }
            components.push_back(comp);
        }
    }

    for (auto& comp : components) {
        if (check(comp)) {
            print_v1(comp);
            return 0;
        }
    }

    vector<int> in_N(V + 1, 0);
    for (int u : N) in_N[u] = 1;
    
    vector<int> comp_only_neighbor(cid + 1, -1);
    for (int i = 0; i < components.size(); ++i) {
        int neighbor_y = -1;
        bool only_one = true;
        for (int u : components[i]) {
            for (int v : adj[u]) {
                if (in_N[v]) {
                    if (neighbor_y == -1) neighbor_y = v;
                    else if (neighbor_y != v) {
                        only_one = false;
                        break;
                    }
                }
            }
            if (!only_one) break;
        }
        if (only_one && neighbor_y != -1) {
            comp_only_neighbor[i+1] = neighbor_y;
        }
    }

    for (int y : N) {
        vector<int> v1;
        v1.push_back(y);
        for (int i = 0; i < components.size(); ++i) {
            if (comp_only_neighbor[i+1] == y) {
                for (int u : components[i]) v1.push_back(u);
            }
        }
        if (check(v1)) {
            print_v1(v1);
            return 0;
        }
    }

    cout << -1 << endl;
    return 0;
}