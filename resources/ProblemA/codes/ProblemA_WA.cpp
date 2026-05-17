#include <iostream>
#include <vector>
#include <cstring>

using namespace std;

const int MOD = 1e9 + 7;
long long dp[21][21][21][21];

int main() {
    int n, k;
    cin >> n >> k;
    vector<int> a(n + 1);
    for (int i = 1; i <= n; i++) cin >> a[i];

    memset(dp, 0, sizeof(dp));
    for (int B1 = 0; B1 <= k; B1++) {
        int S1 = B1, K1 = B1;
        for (int E1 = 0; E1 <= S1; E1++) {
            dp[1][S1][S1-E1][K1] = (dp[1][S1][S1-E1][K1] + 1) % MOD;
        }
    }

    for (int i = 1; i < n; i++) {
        for (int Si = 0; Si <= k; Si++) {
            for (int activei = 0; activei <= Si; activei++) {
                for (int Ki = 0; Ki <= k; Ki++) {
                    if (dp[i][Si][activei][Ki] == 0) continue;
                    for (int Snext = 0; Snext <= k; Snext++) {
                        if (a[i+1] + Snext <= a[i] + Si) continue;
                        int Bnext = Snext - activei;
                        if (Bnext < 0 || Ki + Bnext > k) continue;
                        for (int activenext = 0; activenext <= Snext; activenext++) {
                            dp[i+1][Snext][activenext][Ki+Bnext] = (dp[i+1][Snext][activenext][Ki+Bnext] + dp[i][Si][activei][Ki]) % MOD;
                        }
                    }
                }
            }
        }
    }
    long long ans = 0;
    for (int Sn = 0; Sn <= k; Sn++) ans = (ans + dp[n][Sn][0][k]) % MOD;
    cout << ans << endl;
    return 0;
}