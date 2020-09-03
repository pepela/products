package com.peranidze.products.app.productsList

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.peranidze.products.R
import com.peranidze.products.app.base.BaseFragment
import com.peranidze.products.app.productsList.adapter.ProductAdapter
import com.peranidze.products.databinding.FragmentProductsListBinding
import com.peranidze.products.domain.Repository
import com.peranidze.products.presentation.EventObserver
import com.peranidze.products.presentation.extension.mapDistinct
import com.peranidze.products.presentation.viewModel.productsList.ProductsListViewModel
import com.peranidze.products.presentation.withFactory
import javax.inject.Inject

class ProductsListFragment : BaseFragment(R.layout.fragment_products_list) {

    @Inject
    lateinit var productAdapter: ProductAdapter

    @Inject
    lateinit var repository: Repository

    private val viewModel: ProductsListViewModel by viewModels {
        withFactory(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(FragmentProductsListBinding.bind(view)) {
            bindInteractions(this)
            observeState(this)
        }
        observeEvents()
    }

    private fun bindInteractions(binding: FragmentProductsListBinding) {
        binding.productsRv.adapter = productAdapter.apply {
            setOnProductItemClickListener {
                viewModel.onProductItemClicked(it.id, it.categoryId)
            }
        }
    }

    private fun observeState(binding: FragmentProductsListBinding) {
        with(binding) {
            viewModel.state.mapDistinct { it.isLoading }.observe(viewLifecycleOwner, {
                loadingPb.isVisible = it
            })
            viewModel.state.mapDistinct { it.isLoading }.observe(viewLifecycleOwner, {

            })
            viewModel.state.mapDistinct { it.listItems }.observe(viewLifecycleOwner, {
                productAdapter.items = it
            })
        }
    }

    private fun observeEvents() {
        viewModel.navigationToDetailEvent.observe(viewLifecycleOwner, EventObserver {
            navigateToProductDetail(it.first, it.second)
        })
    }

    private fun navigateToProductDetail(id: Long, categoryId: Long) {
        findNavController().navigate(
            ProductsListFragmentDirections.actionProductsListFragmentToProductDetailFragment(
                id,
                categoryId
            )
        )
    }
}
