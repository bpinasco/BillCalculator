package edu.stanford.bpinasco.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    private lateinit var etBillAmount: EditText
    private lateinit var tvTipPercent: TextView
    private lateinit var seekBarTip: SeekBar
    private lateinit var tvTipAmount: TextView
    private lateinit var tvTotalAmount: TextView
    private lateinit var tvTipDescription: TextView
    private lateinit var tvTaxLabel: TextView
    private lateinit var etTaxesValue: EditText
    private lateinit var etP1BaseAmount: EditText
    private lateinit var etP2BaseAmount: EditText
    private lateinit var tvP1TotalAmount: TextView
    private lateinit var tvP2TotalAmount: TextView
    private lateinit var tvCautionMessage: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etBillAmount = findViewById(R.id.etBillAmount)
        tvTipPercent = findViewById(R.id.tvTipPercent)
        seekBarTip = findViewById(R.id.seekBarTip)
        tvTipAmount = findViewById(R.id.tvTipAmount)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        tvTipDescription = findViewById(R.id.tvTipDescription)
        tvTaxLabel = findViewById(R.id.tvTaxLabel)
        etTaxesValue = findViewById(R.id.etTaxesValue)
        etP1BaseAmount = findViewById(R.id.etP1BaseAmount)
        etP2BaseAmount = findViewById(R.id.etP2BaseAmount)
        tvP1TotalAmount = findViewById(R.id.tvP1TotalAmount)
        tvP2TotalAmount = findViewById(R.id.tvP2TotalAmount)
        tvCautionMessage = findViewById(R.id.tvCautionMessage)


        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)

        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                computeTipAndTotal()
                computePerson1()
                computePerson2()
                cautionMessage()
                updateTipDescription(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        etBillAmount.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int){}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG,"afterTextChanged $s")
                computeTipAndTotal()
                computePerson1()
                computePerson2()
                cautionMessage()

            }

        })

        etTaxesValue.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTaxChanged $s")
                computeTipAndTotal()
                computePerson1()
                computePerson2()
                cautionMessage()
            }
        })

        etP1BaseAmount.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                computePerson1()
                cautionMessage()
            }
        })

        etP2BaseAmount.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {
                computePerson2()
                cautionMessage()
            }
        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val TipDescriptionAnimation = when(tipPercent){
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }

        tvTipDescription.text = TipDescriptionAnimation

        // Update the color based on the Tip %
        val colorTipDescription = ArgbEvaluator().evaluate(
            tipPercent.toFloat()/seekBarTip.max,
            ContextCompat.getColor(this, R.color.color_worst_tip),
            ContextCompat.getColor(this, R.color.color_best_tip)
        ) as Int
        tvTipDescription.setTextColor(colorTipDescription)

    }

    private fun computeTipAndTotal() {
        // 0. Check if etBillAmount is 0
        if(etBillAmount.text.isEmpty() || etTaxesValue.text.isEmpty()){
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }

        // 1. Get the value of the base and tip percent
        val Base = etBillAmount.text.toString().toDouble()
        val Tax = etTaxesValue.text.toString().toDouble()
        val TotalBill = Base + Tax
        val TipPercent = seekBarTip.progress
        // 2. Compute the tip and total
        val Tip = TotalBill * TipPercent / 100
        val Total = TotalBill + Tip
        // 3. Update the UI
        tvTipAmount.text = "%.2f".format(Tip)
        tvTotalAmount.text = "%.2f".format(Total)


    }

    private fun computePerson1() {
        // 0. Check if etP1BaseAmount is 0
        if(etP1BaseAmount.text.isEmpty()) {
            tvP1TotalAmount.text = ""
            return
        }

        val Base1 = etBillAmount.text.toString().toDouble()
        val BaseP1 = etP1BaseAmount.text.toString().toDouble()
        val Tax1 = etTaxesValue.text.toString().toDouble()
        val Tip1 = tvTipAmount.text.toString().toDouble()
        val TotalP1 = (BaseP1/Base1)*(Tax1+Tip1) + BaseP1
        tvP1TotalAmount.text = "%.2f".format(TotalP1)
    }

    private fun computePerson2() {
        // 0. Check if etP1BaseAmount is 0
        if (etP2BaseAmount.text.isEmpty()) {
            tvP2TotalAmount.text = ""
            return
        }

        val Base1 = etBillAmount.text.toString().toDouble()
        val BaseP2 = etP2BaseAmount.text.toString().toDouble()
        val Tax1 = etTaxesValue.text.toString().toDouble()
        val Tip1 = tvTipAmount.text.toString().toDouble()
        val TotalP2 = (BaseP2 / Base1) * (Tax1 + Tip1) + BaseP2
        tvP2TotalAmount.text = "%.2f".format(TotalP2)

    }

    private fun cautionMessage() {
        if (tvP2TotalAmount.text.isEmpty() || tvP1TotalAmount.text.isEmpty()) {
            tvCautionMessage.text = ""
            return
        }

        val BaseP1 = etP1BaseAmount.text.toString().toDouble()
        val BaseP2 = etP2BaseAmount.text.toString().toDouble()
        val Base = etBillAmount.text.toString().toDouble()
        if (BaseP1 + BaseP2 == Base) {
            tvCautionMessage.text = ""
        } else {
            tvCautionMessage.text = "Breakdown does not add up to total bill"
        }
    }

}