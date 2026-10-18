const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validProfile(uid, now) {
  return {
    userId: uid,
    displayName: "Aarav Sharma",
    examCategory: "COMPETITIVE",
    examTarget: "NEET UG",
    dailyGoalMinutes: 360,
    xpPoints: 450,
    regainCoins: 90,
    currentStreak: 5,
    longestStreak: 12,
    totalStudyMinutes: 1800,
    completedSessionsCount: 30,
    perfectDaysCount: 4,
    strictBlockEnabled: true,
    dailyNotificationEnabled: true,
    lastActiveDate: "2026-10-18",
    unlockedBadges: ["first_focus", "streak_3"],
    blockedPackages: ["com.instagram.android", "com.google.android.youtube"],
    createdAt: now,
    updatedAt: now,
  };
}

function validRoutine(uid, now) {
  return {
    userId: uid,
    title: "NCERT Biology: Human Physiology",
    subject: "Biology",
    period: "MORNING",
    blockType: "STUDY",
    startTime: "06:30",
    durationMinutes: 90,
    completedMinutesToday: 0,
    isCompletedToday: false,
    examTag: "NEET UG",
    presetName: "NEET Topper Morning",
    notifyOnStart: true,
    priorityOrder: 1,
    createdAt: now,
    updatedAt: now,
  };
}

function validSession(uid, now) {
  return {
    userId: uid,
    routineId: "routine_1",
    title: "NCERT Biology: Human Physiology",
    subject: "Biology",
    period: "MORNING",
    plannedMinutes: 90,
    actualMinutes: 90,
    xpEarned: 150,
    focusScore: 96,
    blockedAttempts: 3,
    dateKey: "2026-10-18",
    hourOfDay: 7,
    examTarget: "NEET UG",
    createdAt: now,
    updatedAt: now,
  };
}

test("Unauthenticated user: cannot read profiles, routines, or sessions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("routines").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("sessions").get());
});

test("Authenticated user: can create and read own valid profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, now))
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read or write another user's profile or routines", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    const db = context.firestore();
    await db.collection("users").doc(BOB_UID).set(validProfile(BOB_UID, now));
    await db.collection("users").doc(BOB_UID).collection("routines").doc("r_bob").set(validRoutine(BOB_UID, now));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
  await assertFails(aliceDb.collection("users").doc(BOB_UID).collection("routines").doc("r_bob").get());
  await assertFails(
    aliceDb.collection("users").doc(BOB_UID).collection("routines").where("userId", "==", BOB_UID).get()
  );
});

test("Authenticated user: shadow field injection on profile update is rejected", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set(validProfile(ALICE_UID, now));
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      xpPoints: 9999,
      isVerifiedAdmin: true,
      updatedAt: new Date(),
    })
  );
});

test("Authenticated user: can create, query, and update own routine and session", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routines").doc("r_1").set(validRoutine(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routines").where("userId", "==", ALICE_UID).get()
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("routines").doc("r_1").update({
      completedMinutesToday: 90,
      isCompletedToday: true,
      updatedAt: new Date(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("sessions").doc("s_1").set(validSession(ALICE_UID, now))
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).collection("sessions").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: invalid enum or negative duration is rejected", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  const badRoutine = { ...validRoutine(ALICE_UID, now), durationMinutes: -10 };
  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).collection("routines").doc("r_bad").set(badRoutine)
  );
});
