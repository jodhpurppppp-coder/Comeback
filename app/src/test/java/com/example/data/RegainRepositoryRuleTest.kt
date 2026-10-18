package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class RegainRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndObserveProfile_authenticatedOwner_succeeds() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = RegainRepository(firestore)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveOrUpdateExamTrack(
                displayName = "Aarav Sharma",
                examCategory = "COMPETITIVE",
                examTarget = "NEET UG",
                dailyGoalMinutes = 360L
            )
        }
        assertTrue("Profile save should succeed: ${saveResult.exceptionOrNull()}", saveResult.isSuccess)
        val profile = saveResult.getOrThrow()
        assertEquals(uid, profile.userId)
        assertEquals("NEET UG", profile.examTarget)
    }

    @Test
    fun createAndObserveRoutine_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = RegainRepository(firestore)

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.createRoutineTask(
                title = "NCERT Biology Ch 1",
                subject = "Biology",
                period = "MORNING",
                blockType = "STUDY",
                startTime = "06:30",
                durationMinutes = 60L,
                examTag = "NEET UG"
            )
        }
        assertTrue("Create routine should succeed: ${createResult.exceptionOrNull()}", createResult.isSuccess)
        val routineId = createResult.getOrThrow()

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeRoutines(uid).first { list -> list.any { it.id == routineId } }
        }
        assertTrue(emitted.any { it.id == routineId })
    }

    @Test
    fun getRoutineById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = RegainRepository(firestore)
        val routineId = withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.createRoutineTask(
                title = "Alice Private JEE Calculus",
                subject = "Mathematics",
                period = "AFTERNOON",
                blockType = "STUDY",
                startTime = "14:00",
                durationMinutes = 90L,
                examTag = "JEE"
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = RegainRepository(firestore)
        val result = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getRoutineById(userId = aliceUid, routineId = routineId)
        }
        assertTrue("Cross-user access must fail", result.isFailure)
        val ex = result.exceptionOrNull() as? FirebaseFirestoreException
        assertNotNull(ex)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, ex?.code)
    }

    @Test
    fun observeRoutines_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = RegainRepository(firestore)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeRoutines(aliceUid).first()
            }
            fail("Expected FirebaseFirestoreException for unauthenticated user")
        } catch (e: FirebaseFirestoreException) {
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, e.code)
        }
    }

    @Test
    fun streakAndMilestoneBadges_earlyBirdAndDeepWorkMaster_unlockAndPersist() = runBlocking {
        val uid = signInTestUser(ALICE_EMAIL)
        val repository = RegainRepository(firestore)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveOrUpdateExamTrack(
                displayName = "Aarav Sharma",
                examCategory = "COMPETITIVE",
                examTarget = "JEE",
                dailyGoalMinutes = 300L
            ).getOrThrow()
        }

        val checkInResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.claimMilestoneOrStreakGoalCheckIn("deep_work_master")
        }
        assertTrue("Milestone check-in should succeed: ${checkInResult.exceptionOrNull()}", checkInResult.isSuccess)

        val updatedProfile = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfile(uid).getOrThrow()
        }
        assertNotNull(updatedProfile)
        assertTrue(updatedProfile!!.currentStreak >= 2L)
        assertTrue(updatedProfile.unlockedBadges.contains("deep_work_master"))
        assertTrue(updatedProfile.unlockedBadges.contains("early_bird"))
    }

    private companion object {
        const val ALICE_EMAIL = "alice_regain@test.com"
        const val BOB_EMAIL = "bob_regain@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
