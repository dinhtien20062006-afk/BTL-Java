#include <iostream>
#include <vector>
#include <numeric>
#include <algorithm>

using namespace std;

int main() {
    int n;
    long long T;
    if (!(cin >> n >> T)) return 0;
    int k = n / 3 + 1;
    vector<int> p(n);
    iota(p.begin(), p.end(), 1);
    vector<vector<int>> res;
    do {
        long long s1 = 0, s2 = 0, s3 = 0;
        for (int i = 0; i <= k - 1; ++i) s1 += p[i];
        for (int i = k - 1; i < 2 * k - 2; ++i) s2 += p[i]; // Miscounting s2: < instead of <=
        for (int i = 2 * k - 2; i < n; ++i) s3 += p[i];
        s3 += p[1]; // Using p[1] instead of p[0]
        if (s1 == T && s2 == T && s3 == T) {
            res.push_back(p);
        }
    } while (next_permutation(p.begin(), p.end()));
    if (res.empty()) {
        cout << -1 << endl;
    } else {
        for (const auto& v : res) {
            for (int i = 0; i < n; ++i) cout << v[i] << (i == n - 1 ? "" : " ");
            cout << endl;
        }
        cout << res.size() << endl;
    }
    return 0;
}