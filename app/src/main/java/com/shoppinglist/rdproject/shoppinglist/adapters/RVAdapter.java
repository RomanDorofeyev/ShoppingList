package com.shoppinglist.rdproject.shoppinglist.adapters;

import android.app.Activity;
import android.graphics.Paint;
import com.google.android.material.snackbar.Snackbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shoppinglist.rdproject.shoppinglist.MainScreen;
import com.shoppinglist.rdproject.shoppinglist.Product;
import com.shoppinglist.rdproject.shoppinglist.R;
import com.shoppinglist.rdproject.shoppinglist.dialogs.ModifyItemDialog;

import java.util.List;


public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int AD_TYPE = 101;
    private static final int CONTENT_TYPE = 202;
    public static final int STATUS_TODO = 0;
    public static final int STATUS_DONE = 1;
    private List<Product> product;
    private int layoutId;

    private Activity context;
    public List<Product> getProductList() {
        return product;
    }

    public RVAdapter(Activity context, List<Product> product, int layoutId){
        this.product = product;
        this.layoutId = layoutId;
        this.context = context;
    }
    @Override
    public  RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AD_TYPE){
            View  v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ads_banner_layout, parent, false);
            return new AdsViewHolder(v);
        } else {

            CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);

            switch (layoutId) {
                case R.id.lis_to_do: {
                    v.setCardBackgroundColor(context.getResources().getColor(R.color.white));
                    return new ProductViewHolder(v);
                }
                case R.id.list_done: {
                    v.setCardBackgroundColor(context.getResources().getColor(R.color.colorMediumGrey));
                }
            }
            return new ProductViewHolder(v);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {
        switch (holder.getItemViewType()) {
            case AD_TYPE:
                AdsViewHolder adsViewHolder = (AdsViewHolder) holder;
                AdRequest request = new AdRequest.Builder().addTestDevice("3361271BC77BB2DAE797507DFCF9F45A").build();
                adsViewHolder.mAdView.loadAd(request);
                break;

            case CONTENT_TYPE:
                ProductViewHolder productViewHolder = (ProductViewHolder) holder;
                String name = product.get(i).getName();
                if (layoutId == R.id.lis_to_do) {
                    productViewHolder.productName.setPaintFlags(Paint.HINTING_OFF);
                } else {
                    productViewHolder.productName.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                }
                productViewHolder.productName.setText(name);
                productViewHolder.productQty.setText(product.get(i).getQuantity());
                productViewHolder.productPhoto.setText(name.substring(0, 1).toUpperCase());
            }
        }

    @Override
    public int getItemViewType(int position)
    {
        if (MainScreen.isAdsfree || MainScreen.isAdsfreeForNow) return CONTENT_TYPE;

        //ads iis disabled at the moment
//        if (position == 5) {
//            return AD_TYPE;
//        }
        return CONTENT_TYPE;
    }

    @Override
    public int getItemCount() {
        return product.size();
    }


    public class AdsViewHolder extends RecyclerView.ViewHolder {
        AdView mAdView;
        public AdsViewHolder(View itemView) {
            super(itemView);
            mAdView = (AdView)itemView.findViewById(R.id.adView);
        }

    }
    public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, AdapterView.OnLongClickListener {
        View cv;
        TextView productName;
        TextView productQty;
        TextView productPhoto;
        ProductViewHolder(View itemView) {
            super(itemView);
            cv = itemView;
            productName = (TextView)itemView.findViewById(R.id.product_name);
            productQty = (TextView)itemView.findViewById(R.id.quantity);
            productPhoto = (TextView)itemView.findViewById(R.id.pic_of_the_product);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Product p = RVAdapter.this.getProductList().get(position);
            if (p.getStatus() == STATUS_TODO){
                p.setStatus(STATUS_DONE);
            }else{
                p.setStatus(STATUS_TODO);
            }
            MainScreen mainScreen = (MainScreen) context;    //  FIREBASE +
            mainScreen.getItemModificationInput(p);

                if (layoutId == R.id.lis_to_do) {
                    Snackbar.make(view, mainScreen.getString(R.string.well_done_you_got) + " " + (p.getName() + "!"), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }


        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();

            ModifyItemDialog modifyItemDialog = new ModifyItemDialog();
            modifyItemDialog.setItemToModify( product.get(position));
            modifyItemDialog.show(((Activity)context).getFragmentManager(), "Modify item");
            return true;
        }
    }
}