package com.melanimolina.thesimpsons

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore


// Importa las clases necesarias
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    //<editor-fold desc="IMAGENES">
    lateinit var iv_11:ImageView
    lateinit var iv_12:ImageView
    lateinit var iv_13:ImageView
    lateinit var iv_14:ImageView

    lateinit var iv_21:ImageView
    lateinit var iv_22:ImageView
    lateinit var iv_23:ImageView
    lateinit var iv_24:ImageView

    lateinit var iv_31:ImageView
    lateinit var iv_32:ImageView
    lateinit var iv_33:ImageView
    lateinit var iv_34:ImageView

    //</editor-fold>

    //<editor-fold desc="PUNTAJES_OTROS">
    lateinit var tv_j1:TextView
    lateinit var tv_j2:TextView

    lateinit var ib_sonido:ImageButton
    lateinit var mp: MediaPlayer
    lateinit var mpFondo: MediaPlayer
   //Para realizar comparaciones entre las dos imagenes
    lateinit var imagen1: ImageView
    lateinit var imagen2: ImageView

    //</editor-fold>

    //<editor-fold desc="VARIABLES_AUX">
        var imagenesArray= arrayOf(11,12,13,14,15,16,21,22,23,24,25,26)

        var homero=0
        var bart=0
        var lisa=0
        var familia=0
        var juntos=0
        var comida=0

        var turno=1
        var puntosJ1=0
        var puntosJ2=0


        var numeroImagen=1
        var escuchar=true

        val idJugador1 = "jugador1"
        val idJugador2 = "jugador2"
    //</editor-fold>

    // Agrega una referencia a la colección principal de Firestore
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enlazarInterfaz()
        sonido("background", true)

    }

    private fun enlazarInterfaz() {
        iv_11 = findViewById(R.id.iv_12)
        iv_12 = findViewById(R.id.iv_13)
        iv_13 = findViewById(R.id.iv_14)
        iv_14 = findViewById(R.id.iv_11)

        iv_21 = findViewById(R.id.iv_21)
        iv_22 = findViewById(R.id.iv_22)
        iv_23 = findViewById(R.id.iv_23)
        iv_24 = findViewById(R.id.iv_24)

        iv_31 = findViewById(R.id.iv_31)
        iv_32 = findViewById(R.id.iv_32)
        iv_33 = findViewById(R.id.iv_33)
        iv_34 = findViewById(R.id.iv_34)

        ib_sonido=findViewById(R.id.ib_sonido)
        ib_sonido.setColorFilter(Color.GREEN)
        sonido("backforund",true)

        iv_11.tag="0"
        iv_12.tag="1"
        iv_13.tag="2"
        iv_14.tag="3"
        iv_21.tag="4"
        iv_22.tag="5"
        iv_23.tag="6"
        iv_24.tag="7"
        iv_31.tag="8"
        iv_32.tag="9"
        iv_33.tag="10"
        iv_34.tag="11"

        homero=R.drawable.homero
        bart=R.drawable.bart
        comida=R.drawable.comida
        familia =R.drawable.familia
        juntos=R.drawable.juntos
        lisa=R.drawable.lisa

        imagenesArray.shuffle()
        tv_j1=findViewById(R.id.tv_j1)
        tv_j2=findViewById(R.id.tv_j2)

        tv_j1.setTextColor(Color.WHITE)
        tv_j2.setTextColor(Color.GRAY)



    }
    //Musica de Fondo
    private fun sonido(sonidoName: String, loop: Boolean = false) {
        val resID = resources.getIdentifier(sonidoName, "raw", packageName)

        if (sonidoName == "background") {
            mpFondo = MediaPlayer.create(this, resID)
            mpFondo.isLooping = loop
            mpFondo.setVolume(4F, 4F)
            if (!mpFondo.isPlaying) {
                mpFondo.start()
            }
        }
    }

    fun musicaFondo(v: View) {
        if (escuchar) {
            mpFondo.pause()
            ib_sonido.setImageResource(R.drawable.silencio)
            ib_sonido.setColorFilter(Color.GRAY)
        } else {
            mpFondo.start()
            ib_sonido.setImageResource(R.drawable.baseline_volume_on)
            ib_sonido.setColorFilter(Color.GREEN)
        }
        escuchar = !escuchar
    }
    //Control de la musica de fondo
    override fun onStop() {
        super.onStop()
        detenerMusicaFondo()
    }

    override fun onDestroy() {
        super.onDestroy()
        liberarRecursos()
    }

    private fun detenerMusicaFondo() {
        if (mpFondo.isPlaying) {
            mpFondo.pause()
        }
    }

    private fun liberarRecursos() {
        if (::mpFondo.isInitialized) {
            mpFondo.stop()
            mpFondo.release()
        }
    }

    fun selecionar(imagen: View) {
        sonido("touch")
        val tag = imagen.tag.toString().toInt()
        verificar(imagen)

        // Almacena la interacción en Firestore
        almacenarInteraccion(tag)
    }
    private fun almacenarInteraccion(tag: Int) {
        val jugadorActual = if (turno == 1) idJugador1 else idJugador2

        val nuevaInteraccion = hashMapOf(
            "tag" to tag,
            "jugador" to jugadorActual,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("interacciones")
            .add(nuevaInteraccion)
            .addOnSuccessListener {
                Log.d("Firestore", "Interacción almacenada correctamente para $jugadorActual")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al almacenar la interacción: $e")
            }
    }

    private  fun verificar(imagen: View){
        var iv=(imagen as ImageView)
        var tag=imagen.tag.toString().toInt()
        if(imagenesArray[tag]==11){
            iv.setImageResource(homero)
        }else if(imagenesArray[tag]==12){
            iv.setImageResource(bart)
        }else if(imagenesArray[tag]==13){
            iv.setImageResource(lisa)
        }else if(imagenesArray[tag]==14){
            iv.setImageResource(familia)
        }else if(imagenesArray[tag]==15){
            iv.setImageResource(comida)
        }else  if(imagenesArray[tag]==16){
            iv.setImageResource(juntos)
        }else if(imagenesArray[tag]==21){
            iv.setImageResource(homero)
        }else if(imagenesArray[tag]==22){
            iv.setImageResource(bart)
        }else if(imagenesArray[tag]==23){
            iv.setImageResource(lisa)
        }else if(imagenesArray[tag]==24){
            iv.setImageResource(familia)
        }else if(imagenesArray[tag]==25){
            iv.setImageResource(comida)
        }else  if(imagenesArray[tag]==26){
            iv.setImageResource(juntos)
        }
        //guardar temporalmente imagen selecionada
        if(numeroImagen==1){
            imagen1= iv
            numeroImagen=2
            iv.isEnabled= false
        }else if(numeroImagen==2){
            imagen2= iv
            numeroImagen=1
            iv.isEnabled= false

            desahabilitarImagenes()
            val h = android.os.Handler(Looper.getMainLooper())
            h.postDelayed({sonImagenesIguales()}, 1000)


        }
    }

    private fun sonImagenesIguales() {
        if (imagen1.drawable.constantState == imagen2.drawable.constantState) {
            sonido("success")

            if (turno == 1) {
                puntosJ1++
            } else if (turno == 2) {
                puntosJ2++
            }

            Log.d("JUEGO", "Puntos después de acertar: J1=$puntosJ1, J2=$puntosJ2")

            actualizarPuntos()

            imagen1.isEnabled = false
            imagen2.isEnabled = false
            imagen1.tag = ""
            imagen2.tag = ""
        } else {
            sonido("no")

            cambiarTurno()

            Log.d("JUEGO", "Puntos después de cambiar turno: J1=$puntosJ1, J2=$puntosJ2")

            imagen1.setImageResource(R.drawable.oculta)
            imagen2.setImageResource(R.drawable.oculta)
        }
        habilitarImagenes()
    }

    private fun cambiarTurno() {
        if (turno == 1) {
            turno = 2
            tv_j1.setTextColor(Color.GRAY)
            tv_j2.setTextColor(Color.WHITE)
        } else if (turno == 2) {
            turno = 1
            tv_j1.setTextColor(Color.WHITE)
            tv_j2.setTextColor(Color.GRAY)
        }
    }

    private fun actualizarPuntos() {
        Log.d("JUEGO", "Texto antes de actualizar: J1=${tv_j1.text}, J2=${tv_j2.text}")

        val textoInicialJ1 = tv_j1.text.toString()
        val textoInicialJ2 = tv_j2.text.toString()

        val nuevoTextoJ1 = if (textoInicialJ1.startsWith("Jugador 1:")) textoInicialJ1.removePrefix("Jugador 1: ") else textoInicialJ1
        val nuevoTextoJ2 = if (textoInicialJ2.startsWith("Jugador 2:")) textoInicialJ2.removePrefix("Jugador 2: ") else textoInicialJ2

        tv_j1.text = "Jugador 1: $puntosJ1"
        tv_j2.text = "Jugador 2: $puntosJ2"

        Log.d("JUEGO", "Texto después de actualizar: J1=${tv_j1.text}, J2=${tv_j2.text}")
    }
    private fun reiniciarJuego() {
        puntosJ1 = 0
        puntosJ2 = 0
        turno = 1
        numeroImagen = 1
        escuchar = true

        imagenesArray.shuffle()

        habilitarImagenes()

        sonido("background", true)

        actualizarPuntos()


    }

    private fun reiniciarTurno() {
        turno = 1
        tv_j1.setTextColor(Color.GRAY)
        tv_j2.setTextColor(Color.WHITE)
    }

    private fun habilitarImagenes() {
        iv_11.isEnabled = iv_11.tag.toString().isNotEmpty()
        iv_12.isEnabled = iv_12.tag.toString().isNotEmpty()
        iv_13.isEnabled = iv_13.tag.toString().isNotEmpty()
        iv_14.isEnabled = iv_14.tag.toString().isNotEmpty()
        iv_21.isEnabled = iv_21.tag.toString().isNotEmpty()
        iv_22.isEnabled = iv_22.tag.toString().isNotEmpty()
        iv_23.isEnabled = iv_23.tag.toString().isNotEmpty()
        iv_24.isEnabled = iv_24.tag.toString().isNotEmpty()
        iv_31.isEnabled = iv_31.tag.toString().isNotEmpty()
        iv_32.isEnabled = iv_32.tag.toString().isNotEmpty()
        iv_33.isEnabled = iv_33.tag.toString().isNotEmpty()
        iv_34.isEnabled = iv_34.tag.toString().isNotEmpty()

        verificarFinJuego()
    }

    private fun verificarFinJuego() {
        if (
            iv_11.tag.toString().isNotEmpty() ||
            iv_12.tag.toString().isNotEmpty() ||
            iv_13.tag.toString().isNotEmpty() ||
            iv_14.tag.toString().isNotEmpty() ||
            iv_21.tag.toString().isNotEmpty() ||
            iv_22.tag.toString().isNotEmpty() ||
            iv_23.tag.toString().isNotEmpty() ||
            iv_24.tag.toString().isNotEmpty() ||
            iv_31.tag.toString().isNotEmpty() ||
            iv_32.tag.toString().isNotEmpty() ||
            iv_33.tag.toString().isNotEmpty() ||
            iv_34.tag.toString().isNotEmpty()
        ) {

            return
        }


        if (::mp.isInitialized && mp.isPlaying) {
            mp.stop()
            mp.release()
        }
        sonido("win")
        val builder = AlertDialog.Builder(this)
        builder
            .setTitle("FIN DEL JUEGO")
            .setMessage("PUNTAJE \nJugador 1: $puntosJ1\nJugador 2: $puntosJ2")
            .setCancelable(false)
            .setPositiveButton("Nuevo Juego",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            .setNegativeButton("SALIR",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    finish()
                })
        val ad = builder.create()
        ad.show()
    }

    private fun desahabilitarImagenes(){
        iv_11.isEnabled=false
        iv_12.isEnabled=false
        iv_13.isEnabled=false
        iv_14.isEnabled=false
        iv_21.isEnabled=false
        iv_22.isEnabled=false
        iv_23.isEnabled=false
        iv_24.isEnabled=false
        iv_31.isEnabled=false
        iv_32.isEnabled=false
        iv_33.isEnabled=false
        iv_34.isEnabled=false
    }


}