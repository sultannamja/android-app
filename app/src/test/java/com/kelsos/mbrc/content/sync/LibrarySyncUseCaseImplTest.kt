package com.kelsos.mbrc.content.sync

import androidx.test.ext.junit.runners.AndroidJUnit4
import arrow.core.Try
import com.google.common.truth.Truth.assertThat
import com.kelsos.mbrc.content.library.albums.AlbumRepository
import com.kelsos.mbrc.content.library.artists.ArtistRepository
import com.kelsos.mbrc.content.library.genres.GenreRepository
import com.kelsos.mbrc.content.library.tracks.TrackRepository
import com.kelsos.mbrc.features.playlists.repository.PlaylistRepository
import com.kelsos.mbrc.metrics.SyncMetrics
import com.kelsos.mbrc.utils.testDispatcherModule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.experimental.builder.singleBy
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class LibrarySyncUseCaseImplTest : KoinTest {

  private val genreRepository: GenreRepository by inject()
  private val artistRepository: ArtistRepository by inject()
  private val albumRepository: AlbumRepository by inject()
  private val trackRepository: TrackRepository by inject()
  private val playlistRepository: PlaylistRepository by inject()
  private val librarySyncUseCase: LibrarySyncUseCase by inject()
  private val syncMetrics: SyncMetrics by inject()

  private val testModule = module {
    singleBy<LibrarySyncUseCase, LibrarySyncUseCaseImpl>()
    single { mockk<GenreRepository>() }
    single { mockk<ArtistRepository>() }
    single { mockk<AlbumRepository>() }
    single { mockk<TrackRepository>() }
    single { mockk<PlaylistRepository>() }
    single { mockk<SyncMetrics>() }
  }

  @Before
  fun setUp() {
    startKoin {
      modules(listOf(testModule, testDispatcherModule))
    }
    every { syncMetrics.librarySyncComplete(any()) } answers { }
    every { syncMetrics.librarySyncStarted() } answers { }
    every { syncMetrics.librarySyncFailed() } answers { }
  }

  @After
  fun tearDown() {
    stopKoin()
  }

  @Test
  fun emptyLibraryAutoSync() {
    mockCacheState(true)
    mockSuccessfulRepositoryResponse()

    val result = runBlocking {
      librarySyncUseCase.sync(true)
    }

    assertThat(result).isEqualTo(SyncResult.SUCCESS)
  }

  @Test
  fun nonEmptyLibraryAutoSync() {
    mockCacheState(false)
    mockSuccessfulRepositoryResponse()

    val result = runBlocking {
      librarySyncUseCase.sync(true)
    }

    assertThat(result).isEqualTo(SyncResult.NOOP)
    assertThat(librarySyncUseCase.isRunning()).isFalse()
  }

  private fun mockCacheState(isEmpty: Boolean) {
    coEvery { genreRepository.cacheIsEmpty() } returns isEmpty
    coEvery { artistRepository.cacheIsEmpty() } returns isEmpty
    coEvery { albumRepository.cacheIsEmpty() } returns isEmpty
    coEvery { trackRepository.cacheIsEmpty() } returns isEmpty
    coEvery { playlistRepository.cacheIsEmpty() } returns isEmpty

    coEvery { genreRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { artistRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { albumRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { trackRepository.count() } returns if (isEmpty) 0 else 1
    coEvery { playlistRepository.count() } returns if (isEmpty) 0 else 1
  }

  private fun mockSuccessfulRepositoryResponse() {
    coEvery { genreRepository.getRemote() } coAnswers {
      delay(1)
      Try.invoke { }
    }
    coEvery { artistRepository.getRemote() } coAnswers {
      delay(1)
      Try.invoke { }
    }
    coEvery { albumRepository.getRemote() } coAnswers {
      delay(1)
      Try.invoke { }
    }
    coEvery { trackRepository.getRemote() } coAnswers {
      delay(1)
      Try.invoke { }
    }
    coEvery { playlistRepository.getRemote() } coAnswers {
      delay(1)
      Try.invoke { }
    }
  }
}