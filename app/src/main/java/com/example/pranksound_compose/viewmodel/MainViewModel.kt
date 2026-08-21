package com.example.pranksoundalpha.viewmodel

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.plant.utils.minhtn.SharePreferenceExt
import com.example.pranksound.data.DataRepositorySource
import com.example.pranksound.data.Resource
import com.example.pranksound.data.dto.localprank.MyFolderAudio
import com.example.pranksound.data.dto.localprank.MyFolderImage
import com.example.pranksound.data.dto.localprank.MyFolderVideo
import com.example.pranksound.data.dto.localprank.MyVideo
import com.example.pranksound.data.dto.prank.CustomSound
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.dto.prank.MergeVideo
import com.example.pranksound.data.dto.prank.PrankCallContact
import com.example.pranksound.data.dto.prank.PrankRecordItem
import com.example.pranksound.data.dto.prank.Sound
import com.example.pranksound.data.dto.prank.SoundFolder
import com.example.pranksound.data.dto.prank.SoundFolderPrank
import com.example.pranksound.data.dto.prank.VideoPrank
import com.example.pranksound.data.dto.response.ResponseCategorySound
import com.example.pranksound.data.dto.response.ResponsePrankCall
import com.example.pranksound.data.dto.response.ResponsePrankRecordFolder
import com.example.pranksound.data.dto.response.ResponsePrankRecordItem
import com.example.pranksound.data.dto.response.ResponseSound
import com.example.pranksound.data.dto.response.ResponseVideo
import com.example.pranksound.utils.SingleEvent
import com.example.pranksound.utils.asLiveData
import com.example.pranksound.utils.wrapEspressoIdlingResource
import com.example.pranksoundalpha.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject
constructor(private val dataRepository: DataRepositorySource) : BaseViewModel() {

    private var soundFolderSave: SoundFolder? = null

    private val _trendingSoundList = MutableStateFlow<List<Sound>>(emptyList())
    val trendingSoundList = _trendingSoundList.asStateFlow()

    private val _soundFolderList = MutableLiveData<Resource<List<SoundFolder>>>()
    val soundFolderList: LiveData<Resource<List<SoundFolder>>> = _soundFolderList

    private val _fullSoundList = MutableStateFlow<List<Sound>>(emptyList())
    val fullSoundList = _fullSoundList.asStateFlow()

    private val _soundList = MutableStateFlow<List<Sound>>(emptyList())
    val soundList = _soundList.asStateFlow()

    private val _favoriteSoundList = MutableStateFlow<List<FavoriteSound>>(emptyList())
    val favoriteSoundList = _favoriteSoundList.asStateFlow()

    private val _customSoundList = MutableStateFlow<List<CustomSound>>(emptyList())
    val customSoundList = _customSoundList.asStateFlow()

    private val _selectedSound = MutableStateFlow<Sound?>(null)
    val selectedSound = _selectedSound.asStateFlow()

    private val _selectedCustomSound = MutableStateFlow<CustomSound?>(null)
    val selectedCustomSound = _selectedCustomSound.asStateFlow()

    private val _selectedTrendingSound = MutableStateFlow<Sound?>(null)
    val selectedTrendingSound = _selectedTrendingSound.asStateFlow()

    private var hasLoadedTrending = false

    init {
        getSoundFolders()
        getAllCustomSound()
        getSounds()
    }

    fun deleteFavoriteSound(favoriteSound: FavoriteSound) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.deleteFavoriteSound(favoriteSound.uniqueId)
        }
    }

    fun getSounds() {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.getSounds().collect {
                _fullSoundList.emit(it)
            }
        }
    }


    fun getTrendingSoundsOnce() {
        if (!hasLoadedTrending) {
            hasLoadedTrending = true
            getTrendingSounds()
        }
    }

    fun insertCustomSound(customSound: CustomSound) {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.insertCustomSound(customSound)
        }
    }

    fun getAllCustomSound() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dataRepository.getAllCustomSound(),
                dataRepository.getAllFavoriteSound()
            ) { customSoundList, favoriteSoundList ->
                val favoriteSet = favoriteSoundList.map { it.uniqueId }.toSet()
                customSoundList
                    .mapIndexed { index, sound ->
                        sound.copy(
                            id = index,
                            isFavorite = favoriteSet.contains(sound.title)
                        )
                    }
            }.collect { resultList ->
                _customSoundList.emit(resultList)

                val currentCustomSound = _selectedCustomSound.value
                if (currentCustomSound != null) selectCustomSound(currentCustomSound)
                else if (resultList.isNotEmpty()) selectCustomSound(resultList[0])
            }

        }
    }

    // MinhTNAPI
    fun getSoundFolders() {
        _soundFolderList.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = dataRepository.getSoundFolders()
                _soundFolderList.postValue(result)
            } catch (e: Exception) {
                _soundFolderList.postValue(Resource.DataError(-1))
            }
        }
    }

    private fun getTrendingSounds() {
        viewModelScope.launch(Dispatchers.IO) {
            dataRepository.getSounds().collect { list ->
                val trendingList = list
                    .groupBy { it.group }
                    .flatMap { (_, groupSounds) ->
                        groupSounds.shuffled().take(5)
                    }
                    .mapIndexed { index, sound -> sound.copy(id = index) } // Nếu cần id

                _trendingSoundList.emit(trendingList)
            }
        }
    }

    fun clearSelectedTrendingSound() {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedTrendingSound.emit(null)
            updateTrendingSoundList()
        }
    }

    fun getSoundsByGroup(soundFolder: SoundFolder) {
        soundFolderSave = soundFolder
        viewModelScope.launch(Dispatchers.IO) {
            _selectedSound.emit(null)

            combine(
                dataRepository.getSounds(),
                dataRepository.getAllFavoriteSound()
            ) { soundList, favoriteSoundList ->
                val favoriteSet = favoriteSoundList.map { it.uniqueId }.toSet()
                soundList.filter { it.group == soundFolderSave!!.group }
                    .mapIndexed { index, sound ->
                        sound.copy(
                            id = index,
                            isFavorite = favoriteSet.contains(sound.name)
                        )
                    }
            }.collect { resultList ->
                _soundList.emit(resultList)
                updateSelectedSoundByGroup(resultList, soundFolderSave!!.group)
            }
        }
    }

    private suspend fun updateSelectedSoundByGroup(
        resultList: List<Sound>,
        newGroup: String
    ) {
        val currentSelected = _selectedSound.value
        val isInSameGroup = currentSelected?.group == newGroup
        val updatedSelected = resultList.find { it.name == currentSelected?.name }

        if (!isInSameGroup || updatedSelected == null) {
            if (resultList.isNotEmpty()) {
                _selectedSound.emit(resultList[0])
                updateSoundList()
            } else {
                _selectedSound.emit(null)
            }
        } else {
            _selectedSound.emit(updatedSelected)
            updateSoundList()
        }
    }

    fun selectTrendingSound(trendingSound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
            val currentKey = _selectedTrendingSound.value?.name
            val newKey = trendingSound.name

            if (currentKey == newKey) {
                _selectedTrendingSound.emit(null)
            } else {
                _selectedTrendingSound.emit(trendingSound)
            }

            updateTrendingSoundList()
        }
    }

    private suspend fun updateTrendingSoundList() {
        val list = mutableListOf<Sound>()
        _trendingSoundList.value.forEach {
            list.add(it.copy(isSelected = it.name == _selectedTrendingSound.value?.name))
        }
        _trendingSoundList.emit(list)
    }

    fun selectSound(sound: Sound) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedSound.emit(sound)
            updateSoundList()
        }
    }

    private suspend fun updateSoundList() {
        val list = mutableListOf<Sound>()
        _soundList.value.forEach {
            list.add(it.copy(isSelected = it.name == _selectedSound.value?.name))
        }
        _soundList.emit(list)
    }

    fun selectCustomSound(sound: CustomSound) {
        viewModelScope.launch(Dispatchers.IO) {
            _selectedCustomSound.emit(sound)
            updateCustomSoundList()
        }
    }

    private suspend fun updateCustomSoundList() {
        val updatedList = _customSoundList.value.map {
            it.copy(isSelected = it.id == _selectedCustomSound.value?.id)
        }
        _customSoundList.emit(updatedList)
    }

    fun getMaxCustomSoundID(): Int {
        return dataRepository.getMaxId()
    }

    fun getAllFavoriteSound() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(
                dataRepository.getSounds(),
                dataRepository.getAllCustomSound(),
                dataRepository.getAllFavoriteSound()
            ) { apiSounds, customSounds, favoriteList ->

                val favoriteSet = favoriteList.map { it.uniqueId }.toSet()

                val displayFromApi = apiSounds.mapIndexed { index, sound ->
                    FavoriteSound(
                        id = index,
                        uniqueId = sound.name,
                        name = sound.name,
                        link = sound.link,
                        uri = null,
                        thumb = sound.thumb,
                        isFavorite = favoriteSet.contains(sound.name),
                        isCustom = false
                    )
                }.filter { it.isFavorite }

                val displayFromCustom = customSounds.map { sound ->
                    FavoriteSound(
                        id = sound.id,
                        uniqueId = sound.title,
                        name = sound.title,
                        uri = sound.uri,
                        link = null,
                        thumb = null,
                        isFavorite = favoriteSet.contains(sound.title),
                        isCustom = true
                    )
                }.filter { it.isFavorite }

                return@combine displayFromApi + displayFromCustom
            }.collect { list ->
                _favoriteSoundList.emit(list)
            }
        }
    }

    fun linkToStore(context: Context) {
        val appPackageName: String =
            SharePreferenceExt.pushUpdate.newPackage.takeIf { it.isNotEmpty() }
                ?: context.packageName
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$appPackageName")
                )
            )
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                )
            )
        }
    }


    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _video = MutableLiveData<VideoPrank>()
    val video = _video.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val soundFolderLiveDataPrivate = MutableLiveData<SoundFolderPrank>()
    val soundFolderLiveData: LiveData<SoundFolderPrank> get() = soundFolderLiveDataPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _itemPrank = MutableLiveData<PrankRecordItem>()
    val itemPrank: LiveData<PrankRecordItem> get() = _itemPrank

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _mergeVideoPrank = MutableLiveData<MergeVideo>()
    val mergeVideoPrank = _mergeVideoPrank.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _categorySound: MutableLiveData<Resource<ResponseCategorySound>> =
        MutableLiveData<Resource<ResponseCategorySound>>()
    val categorySound = _categorySound.asLiveData()


    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _Sound = MutableLiveData<Resource<ResponseSound>>()
    val Sound = _Sound.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val SoundFavoriteLiveDataPrivate = MutableLiveData<Resource<ResponseSound>>()
    val SoundFavotiteLiveData: LiveData<Resource<ResponseSound>> get() = SoundFavoriteLiveDataPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    //videoPrank
    private val _videoPrank: MutableLiveData<Resource<ResponseVideo>> = MutableLiveData()
    val videoPrank: LiveData<Resource<ResponseVideo>> = _videoPrank

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _callContactPrank = MutableLiveData<Resource<ResponsePrankCall>>()
    val callContactPrank = _callContactPrank.asLiveData()


    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val contactLiveDataPrivate = MutableLiveData<PrankCallContact>()
    val contactLiveData: LiveData<PrankCallContact> get() = contactLiveDataPrivate

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _allImage = MutableLiveData<Resource<List<MyFolderImage>>>()
    val allImage = _allImage.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _allAudio = MutableLiveData<Resource<List<MyFolderAudio>>>()
    val allAudio = _allAudio.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _allVideo = MutableLiveData<Resource<List<MyFolderVideo>>>()
    val allVideo = _allVideo.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _categoryGif = MutableLiveData<Resource<ResponsePrankRecordFolder>>()
    val categoryGif = _categoryGif.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _categoryVideo = MutableLiveData<Resource<ResponsePrankRecordFolder>>()
    val categoryVideo = _categoryVideo.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _itemPrankRecordVideo = MutableLiveData<Resource<ResponsePrankRecordItem>>()
    val itemPrankRecordVideo = _itemPrankRecordVideo.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _itemPrankRecordGif = MutableLiveData<Resource<ResponsePrankRecordItem>>()
    val itemPrankRecordGif = _itemPrankRecordGif.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _allVideoFromFolder = MutableLiveData<Resource<List<MyVideo>>>()
    val allVideoFromFolder = _allVideoFromFolder.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val _itemVideoResult = MutableLiveData<MyVideo>()
    val itemVideoResult = _itemVideoResult.asLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.Companion.PRIVATE)
    private val showToastPrivate = MutableLiveData<SingleEvent<Any>>()
    val showToast: LiveData<SingleEvent<Any>> get() = showToastPrivate


    fun setVideoLiveDate(video: VideoPrank) {
        _video.value = video
    }

//    fun setSoundLiveDate(sound: ArrayList<Sound>) {
//        _sound.value = sound
//    }

    fun setSoundFolderLiveDate(sound: SoundFolderPrank) {
        soundFolderLiveDataPrivate.value = sound
    }

    fun setItemPrankLiveDate(item: PrankRecordItem) {
        _itemPrank.value = item
    }

    fun setItemVideoResultLiveDate(item: MyVideo) {
        _itemVideoResult.value = item
    }

    fun setMergePrankLiveDate(item: MergeVideo) {
        _mergeVideoPrank.value = item
    }

    fun setContactLiveDate(contact: PrankCallContact) {
        contactLiveDataPrivate.value = contact
    }

    fun getAllImage() {
        Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllImage() - Start")
        viewModelScope.launch {
            _allImage.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.getAllImage().collect {
                    Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllImage() - Collect: $it")
                    _allImage.value = it
                }
            }
        }
    }

    fun getAllAudio() {
        Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllAudio() - Start")
        viewModelScope.launch {
            _allAudio.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.getAllAudio().collect {
                    Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllAudio() - Collect: $it")
                    _allAudio.value = it
                }
            }
        }
    }

    fun getAllVideo() {
        Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllVideo() - Start")
        viewModelScope.launch {
            _allVideo.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.getAllVideo().collect {
                    Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllVideo() - Collect: $it")
                    _allVideo.value = it
                }
            }
        }
    }

    fun getAllVideoFromFolder(path: String) {
        Log.d("MinhTN912 - LOGIC", "MainViewModel.getAllVideoFromFolder() - Start")
        viewModelScope.launch {
            _allVideoFromFolder.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.getAllVideoFromFolder(path).collect {
                    Log.d(
                        "MinhTN912 - LOGIC",
                        "MainViewModel.getAllVideoFromFolder() - Collect: $it"
                    )
                    _allVideoFromFolder.value = it
                }
            }
        }
    }

    fun getAllCategoryVideo() {
        viewModelScope.launch {
            _categoryVideo.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.requestCategoryVideo(
                    "id==\"" + URLEncoder.encode(
                        "**",
                        "UTF-8"
                    ) + "\""
                ).collect {
                    _categoryVideo.value = it
                }
            }
        }
    }

    fun getAllCategoryGif() {
        viewModelScope.launch {
            _categoryGif.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.requestCategoryGif(
                    "id==\"" + URLEncoder.encode(
                        "**",
                        "UTF-8"
                    ) + "\""
                ).collect {
                    _categoryGif.value = it
                }
            }
        }
    }

    fun getAllItemPrankRecordVideo() {
        viewModelScope.launch {
            _itemPrankRecordVideo.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.requestItemVideo(
                    "cate.id==\"" + URLEncoder.encode(
                        "**",
                        "UTF-8"
                    ) + "\""
                ).collect {
                    _itemPrankRecordVideo.value = it
                }
            }
        }
    }

    fun getAllItemPrankRecordGif() {
        viewModelScope.launch {
            _itemPrankRecordGif.value = Resource.Loading()
            wrapEspressoIdlingResource {
                dataRepository.requestItemGif(
                    "cate.id==\"" + URLEncoder.encode(
                        "**",
                        "UTF-8"
                    ) + "\""
                ).collect {
                    _itemPrankRecordGif.value = it
                }
            }
        }
    }

    fun showToastMessage(errorCode: Int) {
        val error = errorManager.getError(errorCode)
        showToastPrivate.value = SingleEvent(error.description)
    }
}