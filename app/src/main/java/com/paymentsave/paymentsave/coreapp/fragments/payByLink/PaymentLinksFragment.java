package com.paymentsave.paymentsave.coreapp.fragments.payByLink;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.paymentsave.paymentsave.AppConstants;
import com.paymentsave.paymentsave.R;
import com.paymentsave.paymentsave.coreapp.fragments.viewModels.TransactionViewModel;
import com.paymentsave.paymentsave.coreapp.utils.DialogHelper;
import com.paymentsave.paymentsave.network.SharedHelper;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkItem;
import com.paymentsave.paymentsave.responses.PBLResponse.LinkResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PaymentLinksFragment extends Fragment {

    private static final String TAG = "PaymentLinksFragment";
    boolean loading = false;
    private Button createLinkBtn;
    private LinearLayout backBtn, progressBarLayout;
    private PaymentLinksAdapter adapter;
    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private List<LinkItem> linkItemList = new ArrayList<>();
    private String pblTrnxNextUrl;

    public PaymentLinksFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_links, container, false);
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createLinkBtn = view.findViewById(R.id.pbl_create_btn);
        progressBarLayout = view.findViewById(R.id.link_page_pb);
        backBtn = view.findViewById(R.id.pbl_list_back_btn);
        recyclerView = view.findViewById(R.id.pbl_item_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireActivity());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new PaymentLinksAdapter(requireActivity(), linkItemList);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // Scroll Condition Apply
                if (!loading) {
                    if (pblTrnxNextUrl != null && !pblTrnxNextUrl.equals("")) {
                        assert linearLayoutManager != null;
                        Log.e(TAG, "onScrolled: " + "Total Size-" + linkItemList.size() + " Last Item Visible-" + linearLayoutManager.findFirstCompletelyVisibleItemPosition());
                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == linkItemList.size() - 6) {
                            loading = true;
                            getNextTransactions(pblTrnxNextUrl);
                        }
                    }
                }
            }
        });

        createLinkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_paymentLinksFragment_to_createPBLFragment);
            }
        });

        adapter.setOnTnxItemClickListener(new PaymentLinksAdapter.OnTnxItemClickListener() {
            @Override
            public void onReportItemClickListener(int position, LinkItem linkItem) {
                DialogHelper.showTransactionDialog(requireContext(), linkItem);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).popBackStack();
            }
        });

        getTransactionList();
    }

    private void getNextTransactions(String transactionNextUrl) {
        progressBarLayout.setVisibility(View.VISIBLE);
        String token = SharedHelper.getToken(requireContext());
        viewModel.getPblNextTnxList(transactionNextUrl, token).observe(this, new Observer<LinkResponse>() {
            @Override
            public void onChanged(LinkResponse linkResponse) {
                loading = false;
                progressBarLayout.setVisibility(View.GONE);
                if (linkResponse != null) {

                    if (linkResponse.getData().getResults().size() > 0) {
                        linkItemList.addAll(linkResponse.getData().getResults());
                    }

                    if (linkResponse.getData().getNext() != null) {
                        pblTrnxNextUrl = linkResponse.getData().getNext();
                    } else {
                        pblTrnxNextUrl = null;
                        linkItemList.add(linkItemList.size(), new LinkItem());
                    }
                }
                adapter.itemChanged(linkItemList);
            }
        });
    }

    private void getTransactionList() {
        progressBarLayout.setVisibility(View.VISIBLE);
        String token = SharedHelper.getToken(requireContext());
        String mid = SharedHelper.getStringData(requireContext(), AppConstants.SharedPref.MID);
        HashMap<String, String> map = new HashMap<>();
        map.put("mid", mid);

        viewModel.getPblTnxList(token, map).observe(requireActivity(), new Observer<LinkResponse>() {
            @Override
            public void onChanged(LinkResponse linkResponse) {
                progressBarLayout.setVisibility(View.GONE);
                linkItemList.clear();
                if (linkResponse != null) {
                    Log.e(TAG, "onChanged: " + linkResponse.getData().getNext().toString());
                    if (linkResponse.getData().getResults().size() > 0) {
                        linkItemList.addAll(linkResponse.getData().getResults());
                    }
                    if (linkResponse.getData().getNext() != null) {
                        pblTrnxNextUrl = linkResponse.getData().getNext().toString();
                    } else {
                        pblTrnxNextUrl = null;
                    }
                }
                adapter.itemChanged(linkItemList);
            }
        });
    }
}