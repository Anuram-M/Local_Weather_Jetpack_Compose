package com.ram.local_weather

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.google.firebase.FirebaseApp
import com.ram.local_weather.screens.WeatherHomeScreen
import com.ram.local_weather.ui.theme.LocalWeatherTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.ArrayList
import java.util.Arrays
import kotlin.collections.HashMap
import kotlin.math.log

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalWeatherTheme {
                WeatherHomeScreen()
            }
        }
//        throw RuntimeException(" Test crash second ")
//        createNotificationChannel(this)
//        merge(intArrayOf(1,3,5,0,0,0), 3, intArrayOf(2,4,6), 3)
//        merge2(intArrayOf(1,3,5,0,0,0), 3, intArrayOf(2,4,6), 3)
//        merge2(intArrayOf(1,2,3,0,0,0), 3, intArrayOf(4,5,6), 3)
//        checkSentencePalindrome("This5 :is }si 6siht")
//        checkSentencePalindrome("0P")
//        removeDuplicatesInArray(intArrayOf(1,2,3,3,4,5))
//        findTheMaximumWater(intArrayOf(1,8,6,2,5,4,8,3,7))
//        reverseString(charArrayOf('a', 'b', 'a', 'b','i'))
//        commonSuffix(arrayOf("Relationship", "Battleship", "turnip", "gossip"))
//        findNeedleInHaystack("ajinomoto", "moto")
//        zigzagConvertion("PAYPALISHIRING", 3)
//        findLongestSubstringWithoutRepeatingCharacters("abracadabra")
//        groupAnagrams(arrayOf("bar", "cat",  "bat", "mouse", "sparrow", "tab"))
////        firstNonRepeatingChar("leetcode")
//        wordPattern("abba", "dog cat cat dog")
////        happyNumber(19)
//        happyNum(19)
//        happyNum(20)
//        happyNum(49)
//        happyNum(79)
//
//        topKFrequent(intArrayOf(4,5,3,4,5,6,3,6,7), 2)
//        topKFrequent(intArrayOf(4,5,3,4,5,6,3,6,7,6), 2)
//        checkInclusion("ab", "batman")
//        checkInclusion("abc", "batman")
//        checkInclusion("abt", "batman")
//        checkInclusion("ab", "eidbaooo")
//        findAnagrams("batman", "tab")
//        findAnagrams("kabakabakabakaba", "abk")
//        minWindow("abracadabra", "cab")
        characterReplacement("AABABBA", 1)
        characterReplacement("AABCCCC", 1)
        numSubarrayProductLessThanK(intArrayOf(3,6,4,8), 5)
        numSubarrayProductLessThanK(intArrayOf(3,6,4,8), 3)
        numSubarrayProductLessThanK(intArrayOf(3,6,4,8), 25)
        numSubarrayProductLessThanK(intArrayOf(10,5,2,6), 100)
        minSubArrayLen(6, intArrayOf(2,3,1,2,4,3))
    }

    override fun onResume() {
        super.onResume()
        Log.d("RESUME", "onResume: app gets resumed")
    }

    fun createNotificationChannel(context: Context) {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "channel_id",
                "MY_NOTIFICATION_CHANNEL",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "sample_description"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}

fun merge(nums1: IntArray, m: Int, nums2: IntArray, n: Int) {

    var firstArraySize = 0
    var secondArraySize = 0
    var mergedArray: Array<Int> = arrayOf()
    while (firstArraySize < m && secondArraySize < n) {
        if (nums1[firstArraySize] > nums2[secondArraySize]) {
            mergedArray += nums2[secondArraySize++]
        } else {
            mergedArray += nums1[firstArraySize++]
        }
    }

    while (firstArraySize < m) {
        mergedArray += nums1[firstArraySize++]
    }

    while (secondArraySize < n) {
        mergedArray += nums2[secondArraySize++]
    }
//    }

    Log.d("ARRAT", "merge: ${nums1.contentToString()}, ${nums2.contentToString()}, ${mergedArray.contentToString()}")



}

fun merge2(nums1: IntArray, m: Int, nums2: IntArray, n: Int) {
    var totalLength = m+n-1
    var firstArrayIndex = m-1
    var secondArrayIndex = n-1
    while(firstArrayIndex >= 0 && secondArrayIndex >= 0) {
        if(nums1[firstArrayIndex] > nums2[secondArrayIndex]) {
            nums1[totalLength--] = nums1[firstArrayIndex--]
        } else {
            nums1[totalLength--] = nums2[secondArrayIndex--]
        }
    }

    while (firstArrayIndex >= 0) {
        nums1[totalLength--] = nums1[firstArrayIndex--]
    }

    while (secondArrayIndex >= 0) {
        nums1[totalLength--] = nums2[secondArrayIndex--]
    }
}

fun checkSentencePalindrome(str : String) {
    Log.d("ARRAT", "checkSentencePalindrome: $str")
//    val checkStr = str.lowercase().replace("[^\\w]".toRegex(), "") // removes all special characters, but keeps words and digits
    val checkStr = str.lowercase().replace("[^A-Za-z]".toRegex(), "") // removes all special characters, but keeps words and digits
    var lastIndex = checkStr.length-1
    var isPalindrome = true
    for(i in 0 until checkStr.length/2) {
        if(checkStr[i]!= checkStr[lastIndex--]) {
            isPalindrome = false
            break
        }
    }
    Log.d("ARRAT", "checkSentencePalindrome: $checkStr , $isPalindrome")
}

fun twoSumConstantSpace(intArray: IntArray, target: Int) : IntArray {
    var first = 0
    var second = intArray.size-1

    while (first<second) {
        val sum = intArray[first] + intArray[second]

        when {
            sum == target -> return intArrayOf(++first, ++second)
            sum < target -> first++
            else -> second--
        }
    }

    return intArrayOf(-1, -1)
}

fun removeDuplicatesInArray(intArray: IntArray) : Int {
    var newArray = intArray.toHashSet()
    Log.d("ARRAT", "removeDuplicatesInArray: $newArray")
    Log.d("ARRAT", "removeDuplicatesInArray: ${newArray.size}")

    return 0
}

fun findTheMaximumWater(intArray: IntArray) : Int {

    var firstElementIndex = 0
    var secondElementIndex = intArray.size-1
    var maxWater = 0
    while (firstElementIndex < secondElementIndex) {
        maxWater = maxOf(maxWater, ((secondElementIndex-firstElementIndex) * (minOf(intArray[firstElementIndex], intArray[secondElementIndex])) ))
        when{
            intArray[firstElementIndex] > intArray[secondElementIndex] -> secondElementIndex--
            intArray[secondElementIndex] > intArray[firstElementIndex] -> firstElementIndex++
            else -> firstElementIndex++
        }
    }

    Log.d("ARRAT", "findTheMaximumWater: $maxWater")

    return maxWater
}

fun reverseString(s: CharArray): Unit {
    Log.d("ARRAT", "reverseString: before ${s.contentToString()}")
    var start = 0
    var end = s.size-1
    while (start < end){
        val temp = s[end]
        s[end] = s[start]
        s[start] = temp
        start++
        end--
    }

    Log.d("ARRAT", "reverseString: after ${s.contentToString()}")
}

fun commonSuffix(array: Array<String>) {
    var commonSuffix = array[0]
    for(index in 1 until array.size-1) {
        while(!array[index].endsWith(commonSuffix)) {
            commonSuffix = commonSuffix.drop(1)
        }
    }

    Log.d("ARRAT", "commonSuffix: $commonSuffix")
}

fun findNeedleInHaystack(str: String, needle: String) : Int {
    if(needle.isEmpty()) return 0
    if(needle.length > str.length) return -1
    for(i in 0..str.length) {
        val subStr = str.subSequence(i, i+needle.length)
        if(subStr == needle) {
            return i
        }
    }

    return -1
    for(i in 0 until str.length-1) {
        val subStr = str.subSequence(i, i+needle.length)
        var totalLoop = 0
        val sb = StringBuilder()
        while (totalLoop < needle.length) {
            if(subStr[totalLoop] != needle[totalLoop]) {
                break
            } else {
                sb.append(subStr[totalLoop])
            }
            totalLoop++
        }
        if(sb.equals(needle)) {
            Log.d("ARRAT", "findNeedleInHaystack: needle is at index : $i")
            return i
        }
    }
    Log.d("ARRAT", "findNeedleInHaystack: no needle found in haystack")
    return -1
}

fun zigzagConvertion(str: String, rows: Int) {
    var rowCount = 0
    var toIncrease = true
    val list = MutableList(rows) { StringBuilder() }
    for(char in str) {
        list[rowCount].append(char)
        if(rowCount == 0) toIncrease = true
        if(rowCount == rows-1) toIncrease = false
        rowCount += if(toIncrease) 1 else -1
    }
    val sb3= StringBuilder()
    for(i in list) {
        sb3.append(i.toString())
    }

    var zigzagStr = ""
    for(i in list) {
        zigzagStr += i
    }

    Log.d("ARRAT", "zigzagConvertion: $zigzagStr")

}

fun findLongestSubstringWithoutRepeatingCharacters(str: String) : Int{

    var longStrLength = 0
    var start = 0
    var end = 0
    val checkStr = HashSet<Char>()
    while(end < str.length) {
        val currentchar = str[end]
        if(checkStr.contains(currentchar)) {
            checkStr.remove(str[start])
            start++
        } else {
            checkStr.add(currentchar)
            end++
            longStrLength = maxOf(longStrLength, end-start)
        }
    }

    return longStrLength

    for(index in str.indices) {
        var checkStr = HashSet<Char>()
        checkStr.add(str[start])
        while (start < end) {
            if (!checkStr.add(str[end])) {
                start++
                break
            } else {
                checkStr.add(str[end++])
            }
        }
        Log.d("ARRAT", "findLongestSubstringWithoutRepeatingCharacters: ${checkStr.toString()}")
//        longStr = maxOf(longStr , checkStr.toString())
    }
    return longStrLength
}

fun groupAnagrams(strList: Array<String>) {
    val hash = HashMap<String,List<String>>()
    val finalList = mutableListOf<List<String>>()
    for(str in strList) {
        val sb = str.toCharArray().sorted().joinToString("")
        hash.put(sb, hash.getOrDefault(sb, emptyList()).plus(str))
    }
    for(keys in hash.keys) {
        finalList.add(hash.get(keys)!!)
    }

}

fun firstNonRepeatingChar(str: String): Int {
    val hash = HashMap<Char, Int>()

    for((i,char) in str.withIndex()) {
        if(!hash.containsKey(char)) {
            hash.put(char, i)
        } else {
            hash.put(char, -1)
        }
    }
    for((i, char) in str.withIndex()) {
        if(hash[char] != -1) {
            return i
        }
    }
    return -1
}

fun wordPattern(pattern: String, s: String) : Boolean {
    val strHash  = HashMap<Char, String>()
    val charHash = HashMap<String, Char>()
    val strArray = s.split(" ")
    for(charIndex in pattern.indices) {
        val char = pattern[charIndex]
        val str = strArray[charIndex]
        if(strHash.containsKey(char)) {
            if(strHash.get(char) != str) {
                return false
            }
        } else if(charHash.containsKey(str)) {
            if(charHash.get(str) != char) {
                return false
            }
        } else {
            charHash[str] = char
            strHash[char] = str
        }
    }
    return true
}

fun happyNumber(num: Int) : Boolean {
    var mNum = num
    while (mNum != 1 && mNum < 1000) {
        val rem = mNum%10
        val div = mNum/10
        mNum = rem*rem + div*div
        Log.d("ARRAT", "happyNumber: $mNum")
    }
    return false
}

fun happyNum(n:Int) : Boolean {
    val numHash = HashMap<Int, Int>()
    var hashIndex = 0
    var mNum = n
    while (mNum != -1 && !numHash.containsKey(mNum)) {
        numHash.put(mNum, hashIndex++)
        var sum = 0
        while (mNum>0) {
            val rem = mNum%10
            sum += rem*rem
            mNum /= 10
        }
        mNum = sum
    }

    if(mNum == 1) {
        return true
    }
    return false
}

fun topKFrequent(nums: IntArray, k: Int): IntArray {

    val hash = HashMap<Int, Int>()
    for(num in nums) {
        hash.put(num, hash.getOrDefault(num, 0).plus(1))
    }

    val sortedHash = hash.entries.sortedByDescending { it.value }.take(k)
    val sortedIntArray = sortedHash.map { it.key }

    return sortedIntArray.toIntArray()
}

fun topKFrequentBucketSort(nums2: IntArray, k: Int) : IntArray {
    val hash = HashMap<Int, Int>()
    for(num in nums2) {
        hash.put(num, hash.getOrDefault(num, 0).plus(1))
    }

    val buckets = Array(nums2.size+1){ mutableListOf<Int>() }
    for((num,freq) in hash) {
        buckets[freq].add(num)
    }

    val resultArray = mutableListOf<Int>()
    for(index in buckets.size-1 downTo 0) {
        for(num in buckets[index]) {
            resultArray.add(num)
            if(resultArray.size == k) {
                return resultArray.toIntArray()
            }
        }
    }
    return intArrayOf()
}

fun checkInclusion(s1: String, s2: String): Boolean {

    if(s1.length > s2.length) return false

    val s1Array = IntArray(26)
    val s2Array = IntArray(26)
    for(index in 0 until  s1.length) {
        s1Array[s1[index]-'a']++
        s2Array[s2[index]-'a']++
    }

    Log.d("ARRAT", "checkInclusion: ${s1Array.contentToString()}")
    Log.d("ARRAT", "checkInclusion: ${s2Array.contentToString()}")

    for(index in s1.length until s2.length) {
        if(windowMatches(s1Array, s2Array)) {
            Log.d("ARRAT", "checkInclusion: contains a permutation")
            return true
        }

        s2Array[s2[index]-'a']++
        s2Array[s2[index-s1.length]-'a']--
    }

    Log.d("ARRAT", "checkInclusion: contains a permutation, ${windowMatches(s1Array, s2Array)}")
    return windowMatches(s1Array,s2Array)
}

fun windowMatches(first: IntArray, second: IntArray) : Boolean {
    for(index in 0 until 26) {
        if(first[index] != second[index]) {
            return false
        }
    }
    return true
}

fun findAnagrams(s: String, p: String): List<Int> {

    if(p.length > s.length) return emptyList()
    val sArray = IntArray(26)
    val pArray = IntArray(26)
    for(index in 0 until p.length) {
        sArray[s[index]-'a']++
        pArray[p[index]-'a']++
    }

    var indexList = ArrayList<Int>()

    if(pArray.contentEquals(sArray)) {
        indexList.add(0)
    }
    for(index in p.length until s.length) {

        sArray[s[index]-'a']++
        sArray[s[index - p.length]-'a']--
        if(pArray.contentEquals(sArray)) {
            indexList.add(index-p.length+1)
        }

    }

    Log.d("ARRAT", "findAnagrams: $indexList")

    return if(indexList.isNotEmpty()) indexList else emptyList()
}

fun containsAnagram(first: IntArray, second: IntArray) : Boolean {
    for(index in 0 until 26) {
        if(first[index] != second[index]) {
            return false
        }
    }
    return true
}

fun minWindow(s: String, t: String): String {

    if(t.length > s.length) return ""

    val tHash = HashMap<Char, Int>()
    for(char in t) {
        tHash.put(char, tHash.getOrDefault(char, 0).plus(1))
    }

    val windowHash = HashMap<Char, Int>()
    val neededCharLength = t.length
    var minLength = Int.MAX_VALUE
    var charsHave = 0
    var left = 0
    var minStartIndex = 0

    for(right in s.indices) {
        val char = s[right]
        Log.d("ARRAT", "minWindow: $char")
        windowHash[char] = windowHash.getOrDefault(char, 0).plus(1)
        if(tHash.containsKey(char) && windowHash[char]==tHash[char]) {
            charsHave++
            Log.d("ARRAT", "minWindow: available char $charsHave , ")
        }



        while (charsHave == neededCharLength) {
            val windowSize = right - left + 1
            if(windowSize < minLength) {
                minLength = windowSize
                minStartIndex = left
            }
            val lChar = s[left]
            windowHash[lChar] = windowHash.getOrDefault(lChar, 0).minus(1)
            if(tHash.containsKey(lChar) && windowHash[lChar]!! < tHash[lChar]!!) {
                charsHave--
            }
            left++
            Log.d("ARRAT", "minWindow: left $left, right $right, windowSize $windowSize, charsHave $charsHave ")

        }
        if(minLength != Int.MAX_VALUE) {
            Log.d(
                "ARRAT",
                "minWindow: substring : ${s.subSequence(minStartIndex, minStartIndex + minLength)} "
            )
        }
    }


    for(key in windowHash.keys) {
        Log.d("ARRAT", "minWindow: $key , ${windowHash[key]}")
    }

    return ""
}

fun characterReplacement2(s: String, k: Int): Int {

    val sb = StringBuilder(s)
    val charRef = s[0]
    var refCount =0
    var freqHash = HashMap<Char, Int>()

    for(char in s) {
        freqHash.put(char, freqHash.getOrDefault(char, 0).plus(1))
    }

    val buckets = Array(s.length+1){ mutableListOf<Char>() }


    for(charIndex in s.indices) {
        if(s[charIndex] != charRef && refCount < k ) {
            sb.setCharAt(charIndex, charRef)
            refCount++
        }
    }

    var left = 0
    val hashMap = HashMap<Char, Int>()
    for(right in s.indices) {
        val char = s[right]
        hashMap[char] = hashMap.getOrDefault(char, 0).plus(1)



    }

    Log.d("ARRAT", "characterReplacement: ${sb.toString()}")
    return 0

}

fun characterReplacement(s: String, k: Int): Int {

    var maxLen = 0
    var left = 0
    var maxCharFreq = 0
    var charCount = IntArray(26)

    for(right in s.indices) {
        val charIndex = s[right] - 'A'
        charCount[charIndex]++

        maxCharFreq = maxOf(maxCharFreq, charCount[charIndex])

        while ((right-left+1) - maxCharFreq > k) {
            charCount[s[left] - 'A']--
            left++
        }

        maxLen = maxOf(maxLen, right-left+1)
    }
    return maxLen
}

fun numSubarrayProductLessThanK(nums: IntArray, k: Int): Int {
    if(k==0) return 0

    var left = 0
    var arrayCount = 0
    var product = 1
    for(right in nums.indices) {
        product *= nums[right]

        while (product >= k) {
            product /= nums[left]
            left++
        }

        arrayCount += right-left+1
    }

    return arrayCount

    Log.d("ARRAT", "numSubarrayProductLessThanK: $arrayCount")
    return 0
}

fun minSubArrayLen(target: Int, nums: IntArray): Int {
    var left = 0
    var minLen = Int.MAX_VALUE
    var sum = 0
    for(right in nums.indices) {
        sum += nums[right]

        while (sum >= target && left <= right) {
            minLen = minOf(minLen,right-left+1)
            sum -= nums[left]
            left++
        }
    }
    return minLen
}
