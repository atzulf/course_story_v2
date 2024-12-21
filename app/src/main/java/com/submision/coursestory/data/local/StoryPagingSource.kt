package com.submision.coursestory.data.local

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.submision.coursestory.data.api.ApiService
import com.submision.coursestory.data.pref.UserPreference
import com.submision.coursestory.data.response.ListStoryItem
import kotlinx.coroutines.flow.first

class StoryPagingSource(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) : PagingSource<Int, ListStoryItem>() {

    private companion object{
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val token = userPreference.getSession().first().token
            val response = apiService.getStories(token, position, params.loadSize)
            val responseData = response.listStory?.filterNotNull() ?: emptyList()

            LoadResult.Page(
                data = responseData,
                prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                nextKey = if (responseData.isEmpty()) null else position + 1
            )
        } catch (exception: Exception) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}