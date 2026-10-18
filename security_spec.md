# Security Specification (Phase 0: Payload-First Security TDD)

## 1. Data Invariants
1. **Strict User Isolation**: Every document under `/users/{userId}`, `/users/{userId}/routines/{routineId}`, and `/users/{userId}/sessions/{sessionId}` belongs exclusively to `userId` (`request.auth.uid == userId` and `data.userId == request.auth.uid`). No user can read, list, create, update, or delete another user's profile, routines, or focus sessions.
2. **Schema & Key Completeness**: Every `create` and `update` operation must pass standalone `isValidUserProfile`, `isValidRoutineTask`, or `isValidFocusSession` validation helpers enforcing exact key allowlists (`hasOnly`), required keys (`hasAll`), string length bounds (`.size()`), integer ranges, enum values, and bounded arrays.
3. **Immutable Identity & Creation Timestamps**: `userId` and `createdAt` cannot be mutated on `update` (`incoming().userId == existing().userId && incoming().createdAt == existing().createdAt`).
4. **Temporal Integrity**: `createdAt` and `updatedAt` must be server timestamps (`is timestamp && <= request.time`).

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Profile Read**: `auth = null` reading `/users/alice_123` -> REJECTED.
2. **Cross-User Routine Read**: `auth.uid = "bob_456"` reading `/users/alice_123/routines/r1` -> REJECTED.
3. **Identity Spoofing on Create**: `auth.uid = "alice_123"` creating `/users/alice_123/routines/r1` with `userId: "bob_456"` -> REJECTED.
4. **Shadow Field Injection on Update**: Adding `isAdmin: true` or `isVerified: true` to `/users/alice_123` -> REJECTED by `affectedKeys().hasOnly(...)` and `keys().hasOnly(...)`.
5. **String Overflow (Denial of Wallet)**: Setting `title` to a 5,000-character string in `RoutineTask` -> REJECTED by `data.title.size() <= 120`.
6. **Invalid Enum Injection**: Setting `examCategory: "HACKED"` or `period: "MIDNIGHT"` -> REJECTED by enum allowlist check.
7. **Negative XP / Duration Poisoning**: Setting `durationMinutes: -50` or `xpPoints: -100` -> REJECTED by numeric range checks.
8. **Unbounded Array Poisoning**: Passing 200 items in `blockedPackages` -> REJECTED by `data.blockedPackages.size() <= 50`.
9. **Array Element Type Poisoning**: Passing `[12345]` (number instead of string) in `unlockedBadges` -> REJECTED by `data.unlockedBadges[0] is string`.
10. **CreatedAt Mutation on Update**: Modifying `createdAt` on an existing `RoutineTask` -> REJECTED by `incoming().createdAt == existing().createdAt`.
11. **Unfiltered List Query on Subcollection**: Querying `/users/alice_123/routines` without matching `userId` ownership -> REJECTED if not owner.
12. **Future Timestamp Spoofing**: Submitting `createdAt` in the future (`> request.time`) -> REJECTED by `incoming().createdAt <= request.time`.
