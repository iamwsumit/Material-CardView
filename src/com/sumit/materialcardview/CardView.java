package com.sumit.materialcardview;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.Options;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.common.HorizontalAlignment;
import com.google.appinventor.components.common.VerticalAlignment;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.ViewUtil;
import com.sumit.materialcardview.helpers.Orientation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@DesignerComponent(
        version = 1,
        versionName = "1",
        description = "This extension allows you to create an androidx card view in AI2.<br> Developed By <a href=\"https://sumitkmr.com\" target =\"_blank\">Sumit</a> with <a href=\"https://community.appinventor.mit.edu/t/fast-an-efficient-way-to-build-extensions/129103\" target=\"_blank\">Fast</a>",
        iconName = "icon.png"
)
public class CardView extends AndroidNonvisibleComponent implements Component {

    private final HashMap<String, CardModel> cards = new HashMap<>();

    public CardView(ComponentContainer container) {
        super(container.$form());
        Verification.verifyListViewComponent(form);
    }

    @SimpleEvent(description = "This event raises when the card view is clicked, returns the ID and the card view that is clicked")
    public void Click(String id, AndroidViewComponent cardView) {
        EventDispatcher.dispatchEvent(this, "Click", id, cardView);
    }

    @SimpleEvent(description = "This event raises when the card view is long clicked, returns the ID and the card view that is clicked")
    public void LongClick(String id, AndroidViewComponent cardView) {
        EventDispatcher.dispatchEvent(this, "LongClick", id, cardView);
    }

    @SimpleFunction(description = "Creates a material card view component with unique ID")
    public void CreateCardView(String id, HVArrangement in) {
        if (cards.containsKey(id))
            throw new IllegalArgumentException("ID already exist please use another id");
        else {
            CardModel card = new CardModel(in);
            card.setClickListener(new ClickListener() {
                @Override
                public void onClick() {
                    Click(id, card);
                }

                @Override
                public void onLongClicked() {
                    LongClick(id, card);
                }
            });
            this.cards.put(id, card);
        }
    }

    @SimpleFunction(description = "Add a child component to the card view with the ID")
    public void AddChildComponentToCard(String id, AndroidViewComponent component) {
        getCard(id).AddView(component);
    }

    private CardModel getCard(String id) {
        if (cards.containsKey(id))
            return cards.get(id);
        throw new RuntimeException("Card view with this ID does not exist");
    }

    @SimpleFunction(description = "Set the horizontal alignment of the card view referenced with the given id")
    public void SetAlignment(String id, @Options(HorizontalAlignment.class) int horizontalAlignment, @Options(VerticalAlignment.class) int verticalAlignment) {
        getCard(id).AlignHorizontal(horizontalAlignment);
        getCard(id).AlignVertical(verticalAlignment);
    }

    @SimpleFunction(description = "Set the background color of the card view referenced with given id")
    public void SetBackgroundColor(String id, int color) {
        getCard(id).BackgroundColor(color);
    }

    @SimpleFunction(description = "Set the corner radius of the card view referenced with given id")
    public void SetCornerRadius(String id, int radius) {
        getCard(id).CornerRadius(radius);
    }

    @SimpleFunction(description = "Set the padding of the card view referenced with given id")
    public void SetContentPaddings(String id, int paddingTop, int paddingLeft, int paddingBottom, int paddingRight) {
        getCard(id).Padding(paddingTop, paddingLeft, paddingBottom, paddingRight);
    }

    @SimpleFunction(description = "Set the clickable property of the card")
    public void SetClickable(String id, boolean clickable) {
        if (clickable) {
            getCard(id).setClickListener(new ClickListener() {
                @Override
                public void onClick() {
                    Click(id, getCard(id));
                }

                @Override
                public void onLongClicked() {
                    LongClick(id, getCard(id));
                }
            });
        } else
            getCard(id).setClickListener(null);

        getCard(id).updateCard();
    }

    @SimpleFunction(description = "Set the elevation of the card view referenced with the given id")
    public void SetElevation(String id, int elevation) {
        getCard(id).Elevation(elevation);
    }

    @SimpleFunction(description = "Makes the card view full clickable")
    public void SetFullClickable(String id, boolean full) {
        getCard(id).FullClickable(full);
    }

    @SimpleFunction(description = "Sets the height of the card view referenced with the id, use -1 for automatic and -2 fill parent")
    public void SetHeight(String id, int height) {
        getCard(id).Height(height);
    }

    @SimpleFunction(description = "Sets the height in percent of the card view referenced with the id, only 0-100")
    public void SetHeightPercent(String id, int percent) {
        getCard(id).Height(percent);
    }

    @SimpleFunction(description = "Sets the width of the card view referenced with the id, use -1 for automatic and -2 fill parent")
    public void SetWidth(String id, int width) {
        getCard(id).Width(width);
    }

    @SimpleFunction(description = "Sets the width percent of the card view referenced with the id, only 0-100")
    public void SetWidthPercent(String id, int percent) {
        getCard(id).WidthPercent(percent);
    }

    @SimpleFunction(description = "Set the orientation of the card view")
    public void SetOrientation(String id, @Options(Orientation.class) int orientation) {
        getCard(id).Orientation(orientation);
    }

    @SimpleFunction(description = "Set the touch/ripple color of the card view referenced with the given id")
    public void SetTouchColor(String id, int color) {
        getCard(id).TouchColor(color);
    }

    public class CardModel extends AndroidViewComponent implements Component, ComponentContainer {
        private final AndroidCardMaker cardView;
        private final LinearLayout container;
        private final ViewGroup viewHolder;
        private final ComponentContainer componentContainer;
        private int backgroundColor = -1;
        private int touchColor = Color.LTGRAY;
        private int radius = 5;
        private int elevation = 5;
        private int horizontal = 1;
        private int vertical = 1;
        private int paddingT = 8;
        private int paddingB = 8;
        private int paddingL = 8;
        private int paddingR = 8;
        private final Handler androidUIHandler;

        public CardModel(ComponentContainer container) {
            super(container);
            this.androidUIHandler = new Handler();
            this.componentContainer = container;
            this.cardView = new AndroidCardMaker(this.componentContainer.$context());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
            int m1 = this.px2dp(8);
            int m2 = this.px2dp(4);
            params.setMargins(m1, m2, m1, m2);
            this.cardView.setLayoutParams(params);
            this.cardView.setUseCompatPadding(true);
            this.cardView.setPreventCornerOverlap(false);
            this.container = new LinearLayout(this.componentContainer.$context());
            this.container.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
            this.cardView.addView(this.container, new ViewGroup.LayoutParams(-1, -1));
            this.viewHolder = new FrameLayout(container.$context());
            this.viewHolder.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
            this.viewHolder.addView(this.cardView);
            this.container.setOrientation(LinearLayout.VERTICAL);
            container.$add(this);
            this.updateCard();

            this.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null)
                        listener.onClick();
                }
            });
            this.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null)
                        listener.onLongClicked();
                    return false;
                }
            });
        }

        private ClickListener listener;

        public void setClickListener(ClickListener listener) {
            this.listener = listener;
        }

        public void AddView(AndroidViewComponent component) {
            ((ViewGroup) component.getView().getParent()).removeView(component.getView());
            this.container.addView(component.getView(), -2, -2);
        }

        public void Orientation(int orientation) {
            this.container.setOrientation(orientation);
        }

        public void FullClickable(boolean full) {
            this.cardView.fullClick = full;
        }

        public int BackgroundColor() {
            return this.backgroundColor;
        }

        public void BackgroundColor(int color) {
            this.backgroundColor = color;
            this.updateCard();
        }

        public void CornerRadius(int radius) {
            this.radius = radius;
            this.updateCard();
        }

        public void Padding(int t, int l, int b, int r) {
            paddingL = l;
            paddingT = t;
            paddingB = b;
            paddingR = r;
            updateCard();
        }

        public void Elevation(int elevation) {
            this.elevation = elevation;
            this.updateCard();
        }

        public void AlignHorizontal(int align) {
            this.horizontal = align;
            this.updateCard();
        }

        public void AlignVertical(int align) {
            this.vertical = align;
            this.updateCard();
        }

        public void TouchColor(int color) {
            this.touchColor = color;
            this.updateCard();
        }

        private void setTouchColor(View view, int i, int i2) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    view.setBackground(new RippleDrawable(ColorStateList.valueOf(i2), view.getBackground(), null));
                    return;
                }
                StateListDrawable stateListDrawable = new StateListDrawable();
                int[] iArr = new int[1];
                iArr[0] = 16842919;
                stateListDrawable.addState(iArr, new ColorDrawable(i2));
                int[] iArr2 = new int[1];
                iArr2[0] = 16842908;
                stateListDrawable.addState(iArr2, new ColorDrawable(i2));
                stateListDrawable.addState(new int[0], new ColorDrawable(i));
                view.setBackground(stateListDrawable);
            } catch (Exception e) {
                Log.e("Material Card View", String.valueOf(e));
            }
        }

        private void updateCard() {
            this.cardView.setCardBackgroundColor(this.backgroundColor);
            this.cardView.setRadius(this.px2dp(this.radius));
            this.cardView.setContentPadding(this.px2dp(this.paddingL), this.px2dp(this.paddingT), this.px2dp(this.paddingR), this.px2dp(this.paddingB));
            this.cardView.setCardElevation(this.px2dp(this.elevation));
            this.cardView.setMaxCardElevation(this.px2dp(this.elevation));
            this.container.setHorizontalGravity(this.horizontal == 1 ? 3 : this.horizontal == 2 ? 5 : 1);
            this.container.setVerticalGravity(vertical == 1 ? 48 : vertical == 3 ? 80 : 16);
            this.setTouchColor(this.cardView, this.backgroundColor, this.touchColor);
            this.cardView.invalidate();
        }

        private int px2dp(int px) {
            return (int) (px * this.componentContainer.$context().getResources().getDisplayMetrics().density);
        }

        @Override
        public View getView() {
            return this.viewHolder;
        }

        @Override
        public Activity $context() {
            return this.componentContainer.$context();
        }

        @Override
        public Form $form() {
            return componentContainer.$form();
        }

        @Override
        public void $add(AndroidViewComponent androidViewComponent) {
            this.container.addView(androidViewComponent.getView(), -2, -2);
            allChilds.add(androidViewComponent);
        }

        @Override
        public void setChildWidth(AndroidViewComponent androidViewComponent, int i) {
            setChildWidth(androidViewComponent, i, 0);
        }

        public void setChildWidth(final AndroidViewComponent androidViewComponent, int i, final int i2) {
            final int i3 = i;
            int Width = this.componentContainer.$form().Width();
            if (Width == 0 && i2 < 2) {
                this.androidUIHandler.postDelayed(new Runnable() {

                    public final void run() {
                        setChildWidth(androidViewComponent, i3, i2 + 1);
                    }
                }, 100);
            }
            int i4 = i3;
            if (i3 <= -1000) {
                i4 = (Width * (-(i3 + 1000))) / 100;
            }
            androidViewComponent.setLastWidth(i4);
            ViewUtil.setChildWidthForVerticalLayout(androidViewComponent.getView(), i4);
        }

        @Override
        public void setChildHeight(final AndroidViewComponent androidViewComponent, int i) {
            final int i2 = i;
            int Height = this.componentContainer.$form().Height();
            if (Height == 0) {
                this.androidUIHandler.postDelayed(new Runnable() {

                    public final void run() {
                        setChildHeight(androidViewComponent, i2);
                    }
                }, 100);
            }
            int i3 = i2;
            if (i2 <= -1000) {
                i3 = (Height * (-(i2 + 1000))) / 100;
            }
            androidViewComponent.setLastHeight(i3);
            ViewUtil.setChildHeightForVerticalLayout(androidViewComponent.getView(), i3);
        }

        private final List<Component> allChilds = new ArrayList<>();

        @Override
        public List<? extends Component> getChildren() {
            return allChilds;
        }
    }

    class AndroidCardMaker extends androidx.cardview.widget.CardView {
        public boolean fullClick = false;

        public AndroidCardMaker(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent param1MotionEvent) {
            return fullClick;
        }
    }


    public interface ClickListener {
        void onClick();

        void onLongClicked();
    }
}
