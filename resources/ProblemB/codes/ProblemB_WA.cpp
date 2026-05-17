#include <iostream>
#include <vector>

using namespace std;

int main() {
    int n;
    cin >> n;
    long long total_luck = 0;
    for (int i = 0; i < n - 1; ++i) {
        int u, v, w;
        cin >> u >> v >> w;
        total_luck += w;
    }
    cout << total_luck << endl;
    return 0;
}