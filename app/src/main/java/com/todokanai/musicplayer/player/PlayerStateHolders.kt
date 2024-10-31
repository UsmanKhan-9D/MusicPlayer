package com.todokanai.musicplayer.player

import android.media.MediaPlayer
import com.todokanai.musicplayer.data.datastore.DataStoreRepository
import com.todokanai.musicplayer.data.room.Music
import com.todokanai.musicplayer.interfaces.MediaInterface
import com.todokanai.musicplayer.myobjects.MyObjects
import com.todokanai.musicplayer.repository.MusicRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

/** player의 looping, currentMusic, shuffled, seed 값은 여기서 가져올 것 **/
class PlayerStateHolders (
    val musicRepo:MusicRepository,
    val dsRepo:DataStoreRepository,
    dummyMusic: Music = MyObjects.dummyMusic
) :MediaInterface{

    val initialSeedNew : Double = 0.1
    val initialPlayListNew = emptyList<Music>()
    val initialMusicNew = dummyMusic
    val initialShuffleNew = false
    val initialLoopNew = false

    override val mediaPlayer = MediaPlayer()

    private val isPlaying = MutableStateFlow<Boolean>(false)
    override val isPlayingHolder: StateFlow<Boolean>
        get() = isPlaying

    fun setIsPlaying(isPlaying:Boolean){
        this.isPlaying.value = isPlaying
    }

    private val currentMusic = MutableStateFlow<Music>(initialMusicNew)
    override val currentMusicHolder : StateFlow<Music>
        get() = currentMusic

    fun setCurrentMusic(music: Music){
        currentMusic.value = music
        CoroutineScope(Dispatchers.IO).launch {
            musicRepo.upsertCurrentMusic(music)
        }
    }

    private val isLooping = MutableStateFlow<Boolean>(initialLoopNew)
    override val isLoopingHolder : StateFlow<Boolean>
        get() = isLooping

    fun setIsLooping(isLooping:Boolean){
        this.isLooping.value = isLooping
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsLooping(isLooping)
        }
    }

    private val isShuffled = MutableStateFlow<Boolean>(initialShuffleNew)
    override val isShuffledHolder : StateFlow<Boolean>
        get() = isShuffled

    fun setShuffle(isShuffled:Boolean){
        this.isShuffled.value = isShuffled
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveIsShuffled(isShuffled)
        }
    }

    private val seed = MutableStateFlow<Double>(initialSeedNew)
    override val seedHolder : StateFlow<Double>
        get() = seed

    fun setSeed(seed: Double){
        this.seed.value = seed
        CoroutineScope(Dispatchers.IO).launch {
            dsRepo.saveRandomSeed(seed)
        }
    }

    /*
    /** Todo: MusicRepo.getAll (Room을 observe) 대신 musicListHolder( Array<Music>)를 사용하도록 변경할것  **/
    override val playListHolder =
        combine(
            musicRepo.getAll,
            isShuffledHolder,
            seedHolder
        ){ musics ,shuffled,seed ->
            println("flow detected")
            modifiedPlayList(musics,shuffled,seed)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.WhileSubscribed(5),
            initialValue = initialPlayListNew
        )

     */

  //  /*
    private val _playListHolder = MutableStateFlow<List<Music>>(initialPlayListNew)
    override val playListHolder: StateFlow<List<Music>>
        get() = _playListHolder

    // */

    private fun modifiedPlayList(musicList:Array<Music>, isShuffled:Boolean, seed:Double):List<Music>{
        if(isShuffled){
            return musicList.sortedBy { it.title }.shuffled(Random(seed.toLong()))
        } else{
            return musicList.sortedBy { it.title }
        }
    }

    suspend fun initValues(){
        setSeed(dsRepo.getSeed())
        setShuffle(dsRepo.isShuffled())
        setIsLooping(dsRepo.isLooping())
        setCurrentMusic(musicRepo.currentMusicNonFlow())
        setPlayList(modifiedPlayList(musicRepo.getAllNonFlow(),isShuffledHolder.value,seedHolder.value))
    }

    suspend fun getInitialMusic() = musicRepo.currentMusicNonFlow()

    fun setPlayList(newList:List<Music>) {
        _playListHolder.value = newList
    }

    val playListTest = combine(
        musicRepo.getAll,
        isShuffledHolder,
        seedHolder
    ){
        musics,shuffled,seed ->
        modifiedPlayList(musics,shuffled,seed)
    }.stateIn(
        scope = CoroutineScope(Dispatchers.Default),
        started = SharingStarted.WhileSubscribed(5),
        initialValue = initialPlayListNew
    )
}