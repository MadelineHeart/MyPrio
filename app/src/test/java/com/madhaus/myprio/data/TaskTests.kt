package com.madhaus.myprio.data

import org.junit.Assert
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class TaskTests {
    val baseTask = Task(
        UUID.randomUUID(),
        title = "A working task",
        basePriority = 3,
        description = "This is a well formed, fully filled out Task.",
        daysToRepeat = 2,
        escalateBy = 1,
        daysToEscalate = 4,
        lastCompletedTimestamp = System.currentTimeMillis()
    )

    @Test
    fun testVerify() {
        Assert.assertTrue(baseTask.verify())

        // Title tests
        cloneTask(baseTask).let {
            it.title = ""
            Assert.assertFalse(it.verify())
        }

        // Base priority tests
        cloneTask(baseTask).let {
            it.basePriority = -1
            Assert.assertFalse(it.verify())
        }
        cloneTask(baseTask).let {
            it.basePriority = 10
            Assert.assertFalse(it.verify())
        }

        // Days to Repeat tests
        cloneTask(baseTask).let {
            it.daysToRepeat = null
            Assert.assertFalse(it.verify())
        }
        cloneTask(baseTask).let {
            it.daysToRepeat = 0
            Assert.assertFalse(it.verify())
        }

        // Escalate by tests
        cloneTask(baseTask).let {
            it.escalateBy = -1
            Assert.assertFalse(it.verify())
        }
        cloneTask(baseTask).let {
            it.escalateBy = 10
            Assert.assertFalse(it.verify())
        }

        // Days to escalate tests
        cloneTask(baseTask).let {
            it.daysToEscalate = null
            Assert.assertFalse(it.verify())
        }
        cloneTask(baseTask).let {
            it.daysToEscalate = 0
            Assert.assertFalse(it.verify())
        }

        // Last Completed Timestamp tests
        cloneTask(baseTask).let {
            it.lastCompletedTimestamp = null
            Assert.assertFalse(it.verify())
        }
        cloneTask(baseTask).let {
            it.lastCompletedTimestamp = -1
            Assert.assertFalse(it.verify())
        }
    }

    @Test
    fun testGetPriority() {
        // Base case acts as if just created, should have no priority
        Assert.assertEquals(0, baseTask.getPriority(System.currentTimeMillis()))

        val dayInMillis = TimeUnit.DAYS.toMillis(1)
        // Test should be 0 if we haven't reached the repeat date
        cloneTask(baseTask).let {
            val timeToRepeat = baseTask.lastCompletedTimestamp!! + (dayInMillis * baseTask.daysToRepeat!!) - 1
            Assert.assertEquals(0, baseTask.getPriority(timeToRepeat))
        }
        // Test should be the base priority on the repeat date
        cloneTask(baseTask).let {
            val timeToRepeat = baseTask.lastCompletedTimestamp!! + (dayInMillis * baseTask.daysToRepeat!!)
            Assert.assertEquals(baseTask.basePriority, baseTask.getPriority(timeToRepeat))
        }
        // Test should be base priority + 1 escalation on first escalation date
        cloneTask(baseTask).let {
            val timeToRepeat = baseTask.lastCompletedTimestamp!! + (dayInMillis * (baseTask.daysToRepeat!! + baseTask.daysToEscalate!!))
            Assert.assertEquals(baseTask.basePriority + baseTask.escalateBy, baseTask.getPriority(timeToRepeat))
        }
        // Test to make sure escalations scale properly
        cloneTask(baseTask).let {
            val timeToRepeat = baseTask.lastCompletedTimestamp!! + (dayInMillis * (baseTask.daysToRepeat!! + (baseTask.daysToEscalate!! * 3)))
            Assert.assertEquals(baseTask.basePriority + (baseTask.escalateBy * 3), baseTask.getPriority(timeToRepeat))
        }
        // Test to make sure priority never exceeds 9, 100000 escalations is at least 300 years
        cloneTask(baseTask).let {
            val timeToRepeat = baseTask.lastCompletedTimestamp!! + (dayInMillis * (baseTask.daysToRepeat!! + (baseTask.daysToEscalate!! * 100000)))
            Assert.assertEquals(9, baseTask.getPriority(timeToRepeat))
        }
    }

    companion object {
        // Helper function to avoid tests changing base value
        fun cloneTask(toClone: Task): Task =
            Task(
                toClone.id,
                toClone.title,
                toClone.basePriority,
                toClone.description,
                toClone.daysToRepeat,
                toClone.escalateBy,
                toClone.daysToEscalate,
                toClone.lastCompletedTimestamp
            )
    }
}