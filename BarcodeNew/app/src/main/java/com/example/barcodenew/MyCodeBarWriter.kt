package com.example.barcodenew

import android.graphics.Color

import java.util.*

class MyCodaBarWriter {
    //スタートキャラクタ、ストップキャラクタ(A,B,C,Dの中から選択する)
    private val START_CHAR = "A"
    private val END_CHAAR = "A"

    //バーコードの両端に設けるマージンサイズ(クワイエットゾーン)を棒幅の何個分かで指定する
    private val SIDE_MARGIN_NUM = 10

    //エレメントの値
    private val WHITE = 0 //白エレメント
    private val BLACK = 1 //黒エレメント

    //変換用情報(CODABARの仕様より作成)
    private val convert0 =
        intArrayOf(BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, WHITE, BLACK, BLACK)
    private val convert1 =
        intArrayOf(BLACK, WHITE, BLACK, WHITE, BLACK, BLACK, WHITE, WHITE, BLACK)
    private val convert2 =
        intArrayOf(BLACK, WHITE, BLACK, WHITE, WHITE, BLACK, WHITE, BLACK, BLACK)
    private val convert3 =
        intArrayOf(BLACK, BLACK, WHITE, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK)
    private val convert4 =
        intArrayOf(BLACK, WHITE, BLACK, BLACK, WHITE, BLACK, WHITE, WHITE, BLACK)
    private val convert5 =
        intArrayOf(BLACK, BLACK, WHITE, BLACK, WHITE, BLACK, WHITE, WHITE, BLACK)
    private val convert6 =
        intArrayOf(BLACK, WHITE, WHITE, BLACK, WHITE, BLACK, WHITE, BLACK, BLACK)
    private val convert7 =
        intArrayOf(BLACK, WHITE, WHITE, BLACK, WHITE, BLACK, BLACK, WHITE, BLACK)
    private val convert8 =
        intArrayOf(BLACK, WHITE, WHITE, BLACK, BLACK, WHITE, BLACK, WHITE, BLACK)
    private val convert9 =
        intArrayOf(BLACK, BLACK, WHITE, BLACK, WHITE, WHITE, BLACK, WHITE, BLACK)
    private val convertA =
        intArrayOf(BLACK, WHITE, BLACK, BLACK, WHITE, WHITE, BLACK, WHITE, WHITE, BLACK)
    private val convertB =
        intArrayOf(BLACK, WHITE, WHITE, BLACK, WHITE, WHITE, BLACK, WHITE, BLACK, BLACK)
    private val convertC =
        intArrayOf(BLACK, WHITE, BLACK, WHITE, WHITE, BLACK, WHITE, WHITE, BLACK, BLACK)
    private val convertD =
        intArrayOf(BLACK, WHITE, BLACK, WHITE, WHITE, BLACK, BLACK, WHITE, WHITE, BLACK)

    //変換用情報をMap型に格納する
    private val mapConvert: Map<String, Any> =
        hashMapOf(
            "0" to convert0,
            "1" to convert1,
            "2" to convert2,
            "3" to convert3,
            "4" to convert4,
            "5" to convert5,
            "6" to convert6,
            "7" to convert7,
            "8" to convert8,
            "9" to convert9,
            "A" to convertA,
            "B" to convertB,
            "C" to convertC,
            "D" to convertD)

    fun encode(targetData: String, width: Int, height: Int): MyBitMatrix {

        //スタートキャラクタ、ストップキャラクタの付与
        val exTargetData = START_CHAR + targetData + END_CHAAR

        //データの変換
        val codeArray = convertData(exTargetData)

        //バーコードデータの生成
        return createBarcode(codeArray, width, height)
    }

    // データを変換する
    private fun convertData(targetData: String): List<Int> {
        val resultArray: MutableList<Int> = ArrayList()

        //サイドマージン(左)
        for (i in 0 until SIDE_MARGIN_NUM) {
            resultArray.add(WHITE)
        }

        //データ変換
        for (i in 0 until targetData.length) {

            // 先頭から1文字づつ取り出す
            val targetChar = targetData.substring(i, i + 1)

            // 取り出した文字を変換する
            joinArray(resultArray, targetChar)

            // キャラクターギャップ
            resultArray.add(WHITE)
        }

        // 最後のキャラクターギャップを削除する
        resultArray.removeAt(resultArray.size - 1)

        //サイドマージン(右)
        for (i in 0 until SIDE_MARGIN_NUM) {
            resultArray.add(WHITE)
        }
        return resultArray
    }

    //Listの末尾に文字を変換した情報を付け加える
    private fun joinArray(
        resultArray: MutableList<Int>,
        targetChar: String
    ) {
        val targetArray = mapConvert[targetChar] as IntArray?
        for (i in targetArray!!.indices) {
            resultArray.add(targetArray[i])
        }
    }

    //バーコード情報を生成する
    private fun createBarcode(
        targetData: List<Int>,
        width: Int,
        height: Int
    ): MyBitMatrix {

        // バー幅の算出
        val multiple = getBarWidth(targetData.size, width)

        // バーコード描画サイズの算出
        val drawPx = targetData.size * multiple

        // 要求幅に合わせるために必要な調整幅を算出する
        val leftSideAdjust = (width - drawPx) / 2

        // 結果を格納するためのBitMatrixをインスタンス化する
        val output = MyBitMatrix(width, height)

        // 制御変数の初期化
        var outputX = leftSideAdjust
        for (inputX in targetData.indices) {
            outputX += if (targetData[inputX] == BLACK) {
                output.setRegion(outputX, 0, multiple, height)
                multiple
            } else {
                multiple
            }
        }
        return output
    }

    // 指定されたピクセル数を超えず、かつ最大限大きく描画するためにバー幅の最適値を算出する
    private fun getBarWidth(barCount: Int, viewWidth: Int): Int {
        var resultMultiple = 1
        var multiple = 1
        while (true) {
            val px = barCount * multiple
            resultMultiple = if (px > viewWidth) {
                break
            } else {
                multiple
            }
            multiple += 1
        }
        return resultMultiple
    }
}