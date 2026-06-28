package com.xiguang.app;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.util.LruCache;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.VelocityTracker;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.PathInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Collator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String PREFS = "desktop_prefs";
    private static final String PREF_LAUNCHER_MODE = "launcher_mode";
    private static final String PREF_HIDE_LABELS = "hide_labels";
    private static final String PREF_LOCK_LAYOUT = "lock_layout";
    private static final String PREF_LOOP_PAGES = "loop_pages";
    private static final String PREF_HAPTIC_FEEDBACK = "haptic_feedback";
    private static final String PREF_FAST_LAUNCH = "fast_launch";
    private static final String PREF_APP_ORDER = "app_order";
    private static final String PREF_DESKTOP_APPS = "desktop_apps";
    private static final String PREF_SORT_MODE = "sort_mode";
    private static final String PREF_PAGE_ANIMATION = "launcher_page_animation";
    private static final String PREF_RECENT_APPS = "recent_apps";
    private static final String PREF_SEARCH_HISTORY = "search_history";
    private static final String PREF_PINNED_QUICK_LAUNCH = "pinned_quick_launch";
    private static final String PREF_HIDDEN_APPS = "hidden_apps";
    private static final String PREF_DESKTOP_THEME = "desktop_theme";
    private static final String PREF_ICON_STYLE = "icon_style";
    private static final String PREF_ICON_SIZE = "icon_size";
    private static final String PREF_LABEL_SIZE = "label_size";
    private static final String PREF_VISUAL_REVISION = "visual_revision";
    private static final String PREF_TRANSPARENT_THEME = "transparent_theme";
    private static final String EXTRA_SHOW_SETTINGS = "show_settings";
    private static final int CURRENT_VISUAL_REVISION = 3;
    private static final int PAGE_ANIM_DEFAULT = 0;
    private static final int PAGE_ANIM_GRID_FLIP = 3;
    private static final int PAGE_ANIM_SHUTTER = 4;
    private static final int PAGE_ANIM_CUT_CARD = 6;
    private static final long PAGE_ANIMATION_MS = 260L;
    private static final long PAGE_ANIMATION_CLEANUP_MS = PAGE_ANIMATION_MS + 190L;
    private static final int PAGE_FLING_VELOCITY_DP = 720;
    private static final float ORIGINAL_CURVE_X1 = 0.34f;
    private static final float ORIGINAL_CURVE_Y1 = 0.69f;
    private static final float ORIGINAL_CURVE_X2 = 0.10f;
    private static final float ORIGINAL_CURVE_Y2 = 1.00f;
    private static final int SORT_CUSTOM = 0;
    private static final int SORT_NAME = 1;
    private static final int SORT_INSTALL_TIME = 2;
    private static final int THEME_CLASSIC = 0;
    private static final int THEME_GRAPHITE = 1;
    private static final int THEME_COPPER = 2;
    private static final int THEME_ORIGINAL_BLUE = 3;
    private static final int THEME_CLASSIC_BLUE_TEXTURE = 4;
    private static final int THEME_PANTONE_2025 = 5;
    private static final int THEME_PANTONE_2024 = 6;
    private static final int THEME_PANTONE_2023 = 7;
    private static final int THEME_PANTONE_2022 = 8;
    private static final int THEME_PANTONE_2021 = 9;
    private static final int THEME_PANTONE_START = THEME_PANTONE_2025;
    private static final int THEME_SMARTISAN_LEGACY_START = 10;
    private static final int THEME_SMARTISAN_START = 40;
    private static final int TEXTURE_SAMPLE_FULL = 0;
    private static final int TEXTURE_SAMPLE_TOP = 1;
    private static final int TEXTURE_SAMPLE_BOTTOM = 2;
    private static final PantoneTheme[] PANTONE_THEMES = {
            new PantoneTheme(2025, "摩卡慕斯", Color.rgb(164, 120, 100), 0, false),
            new PantoneTheme(2024, "柔和桃", Color.rgb(255, 190, 152), 0, true),
            new PantoneTheme(2023, "非凡洋红", Color.rgb(190, 52, 85), 0, false),
            new PantoneTheme(2022, "长春花蓝", Color.rgb(102, 103, 171), 0, false),
            new PantoneTheme(2021, "极致灰 / 亮丽黄", Color.rgb(147, 149, 151), Color.rgb(245, 223, 77), true),
            new PantoneTheme(2020, "经典蓝", Color.rgb(15, 76, 129), 0, false),
            new PantoneTheme(2019, "活珊瑚橘", Color.rgb(255, 111, 97), 0, true),
            new PantoneTheme(2018, "紫外光", Color.rgb(95, 75, 139), 0, false),
            new PantoneTheme(2017, "草木绿", Color.rgb(136, 176, 75), 0, true),
            new PantoneTheme(2016, "水晶粉 / 静谧蓝", Color.rgb(247, 202, 201), Color.rgb(146, 168, 209), true),
            new PantoneTheme(2015, "玛萨拉酒红", Color.rgb(149, 82, 81), 0, false),
            new PantoneTheme(2014, "兰花紫", Color.rgb(181, 101, 167), 0, false),
            new PantoneTheme(2013, "翡翠绿", Color.rgb(0, 155, 119), 0, false),
            new PantoneTheme(2012, "探戈橘", Color.rgb(221, 65, 36), 0, false),
            new PantoneTheme(2011, "忍冬红", Color.rgb(214, 80, 118), 0, false),
            new PantoneTheme(2010, "松石绿", Color.rgb(69, 184, 172), 0, true),
            new PantoneTheme(2009, "含羞草黄", Color.rgb(239, 192, 80), 0, true),
            new PantoneTheme(2008, "鸢尾蓝", Color.rgb(91, 94, 166), 0, false),
            new PantoneTheme(2007, "辣椒红", Color.rgb(155, 27, 48), 0, false),
            new PantoneTheme(2006, "沙滩米", Color.rgb(222, 205, 190), 0, true),
            new PantoneTheme(2005, "蓝松石", Color.rgb(85, 180, 176), 0, true),
            new PantoneTheme(2004, "虎百合橙", Color.rgb(225, 93, 68), 0, false),
            new PantoneTheme(2003, "水色天空", Color.rgb(127, 205, 205), 0, true),
            new PantoneTheme(2002, "正红", Color.rgb(188, 36, 60), 0, false),
            new PantoneTheme(2001, "紫红玫瑰", Color.rgb(195, 68, 122), 0, false),
            new PantoneTheme(2000, "蔚蓝色", Color.rgb(155, 183, 212), 0, true)
    };
    private static final SmartisanTheme[] SMARTISAN_THEMES = {
            new SmartisanTheme("blue", R.string.theme_smartisan_blue),
            new SmartisanTheme("lightblue", R.string.theme_smartisan_lightblue),
            new SmartisanTheme("aero", R.string.theme_smartisan_aero),
            new SmartisanTheme("grid", R.string.theme_smartisan_grid),
            new SmartisanTheme("leaf", R.string.theme_smartisan_leaf),
            new SmartisanTheme("darkwood", R.string.theme_smartisan_darkwood),
            new SmartisanTheme("lightgold", R.string.theme_smartisan_lightgold),
            new SmartisanTheme("bluegreen", R.string.theme_smartisan_bluegreen),
            new SmartisanTheme("darkgray", R.string.theme_smartisan_darkgray),
            new SmartisanTheme("deepblue", R.string.theme_smartisan_deepblue),
            new SmartisanTheme("fibre", R.string.theme_smartisan_fibre),
            new SmartisanTheme("lake", R.string.theme_smartisan_lake),
            new SmartisanTheme("bamboo", R.string.theme_smartisan_bamboo),
            new SmartisanTheme("raven", R.string.theme_smartisan_raven),
            new SmartisanTheme("winered", R.string.theme_smartisan_winered),
            new SmartisanTheme("indigo", R.string.theme_smartisan_indigo),
            new SmartisanTheme("leather", R.string.theme_smartisan_leather),
            new SmartisanTheme("lightwood", R.string.theme_smartisan_lightwood),
            new SmartisanTheme("red", R.string.theme_smartisan_red),
            new SmartisanTheme("orange", R.string.theme_smartisan_orange),
            new SmartisanTheme("yellow", R.string.theme_smartisan_yellow),
            new SmartisanTheme("green", R.string.theme_smartisan_green),
            new SmartisanTheme("cyan", R.string.theme_smartisan_cyan),
            new SmartisanTheme("purple", R.string.theme_smartisan_purple),
            new SmartisanTheme("strip", R.string.theme_smartisan_strip),
            new SmartisanTheme("clay", R.string.theme_smartisan_clay),
            new SmartisanTheme("LiteraryBrown", R.string.theme_smartisan_literary_brown),
            new SmartisanTheme("LiteraryCyan", R.string.theme_smartisan_literary_cyan),
            new SmartisanTheme("LiteraryGreen", R.string.theme_smartisan_literary_green),
            new SmartisanTheme("LiteraryPink", R.string.theme_smartisan_literary_pink),
            new SmartisanTheme("LiteraryPurple", R.string.theme_smartisan_literary_purple),
            new SmartisanTheme("LiteraryRed", R.string.theme_smartisan_literary_red),
            new SmartisanTheme("LiteraryWhite", R.string.theme_smartisan_literary_white),
            new SmartisanTheme("LiteraryYellow", R.string.theme_smartisan_literary_yellow)
    };
    private static final int ICON_STYLE_ORIGINAL = 0;
    private static final int ICON_STYLE_PLATE = 1;
    private static final int ICON_SIZE_SMALL = 0;
    private static final int ICON_SIZE_STANDARD = 1;
    private static final int ICON_SIZE_LARGE = 2;
    private static final int LABEL_SIZE_SMALL = 0;
    private static final int LABEL_SIZE_STANDARD = 1;
    private static final int LABEL_SIZE_LARGE = 2;
    private static final String[] CHINESE_INITIAL_BOUNDARIES = {
            "阿", "芭", "擦", "搭", "蛾", "发", "噶", "哈", "击", "喀", "垃", "妈",
            "拿", "哦", "啪", "期", "然", "撒", "塌", "挖", "昔", "压", "匝"
    };
    private static final char[] CHINESE_INITIAL_LETTERS = {
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z'
    };
    private static final Collator CHINESE_INITIAL_COLLATOR = Collator.getInstance(Locale.CHINA);
    private static final int DOCK_PHONE = 0;
    private static final int DOCK_SETTINGS = 1;
    private static final int DOCK_MESSAGES = 2;
    private static final int ACTION_HOME = 0;
    private static final int ACTION_DOCK = 1;
    private static final int ACTION_INFO = 2;
    private static final int ACTION_HIDE = 3;
    private static final int ACTION_UNINSTALL = 4;
    private static final int DROP_TARGET_DESKTOP = 0;
    private static final int DROP_TARGET_DOCK = 1;
    private static final int SETTINGS_PAGE_MAIN = 0;
    private static final int SETTINGS_PAGE_THEME = 1;
    private static final int SETTINGS_PAGE_MANAGEMENT = 2;
    private static final int SETTINGS_PAGE_ANIMATION = 3;
    private static final int SETTINGS_PAGE_ICON_STYLE = 4;
    private static final int SETTINGS_PAGE_ICON_SIZE = 5;
    private static final int SETTINGS_PAGE_LABEL_SIZE = 6;
    private static final int SETTINGS_PAGE_SORT = 7;
    private static final int SETTINGS_PAGE_HIDDEN_APPS = 8;
    private static final int SETTINGS_PAGE_QUICK_LAUNCH = 9;
    private static final int SETTINGS_PAGE_USAGE = 10;
    private static final int SETTINGS_PAGE_RESET = 11;
    private static final int SETTINGS_PAGE_ABOUT = 12;
    private static final long VIEW_TRANSITION_MS = 240L;
    private static final int ICON_LOAD_THREADS = 3;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final ExecutorService appExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService iconExecutor = Executors.newFixedThreadPool(ICON_LOAD_THREADS);
    private final ExecutorService textureExecutor = Executors.newSingleThreadExecutor();
    private final List<AppEntry> allApps = new ArrayList<>();
    private final List<AppEntry> desktopApps = new ArrayList<>();
    private final List<AppEntry> filteredApps = new ArrayList<>();
    private final Map<String, AppEntry> appEntryCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Long> firstInstallTimeCache = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Integer> sampledTextureColorCache = Collections.synchronizedMap(new HashMap<>());
    private final Set<String> desktopAppKeySet = new HashSet<>();
    private final Set<String> hiddenAppKeySet = new HashSet<>();
    private final Set<String> pinnedQuickLaunchKeySet = new HashSet<>();

    private SharedPreferences preferences;
    private FrameLayout root;
    private View desktopView;
    private View searchView;
    private View settingsView;
    private LinearLayout desktopPage;
    private LinearLayout desktopTopBar;
    private WindowInsets desktopWindowInsets;
    private FrameLayout desktopGridLayer;
    private GridView desktopGrid;
    private LinearLayout desktopDock;
    private LinearLayout desktopQuickLaunchPanel;
    private LinearLayout desktopQuickLaunchRow;
    private LinearLayout pageIndicator;
    private DesktopAdapter desktopAdapter;
    private AppAdapter searchAdapter;
    private EditText searchBox;
    private TextView searchStatus;
    private TextView allAppsTitle;
    private TextView allAppsActionButton;
    private TextView desktopStatus;
    private TextView desktopHint;
    private ListView allAppsListView;
    private FrameLayout allAppsListLayer;
    private LinearLayout allAppsDropTray;
    private AppEntry pendingAllAppsDropApp;
    private LinearLayout alphabetIndexBar;
    private final List<TextView> alphabetIndexItems = new ArrayList<>();
    private LinearLayout desktopLoadingOverlay;
    private ImageView desktopLoadingIcon;
    private TextView desktopLoadingText;
    private DesktopLoadingDrawable desktopLoadingDrawable;
    private LinearLayout recentAppsPanel;
    private LinearLayout recentAppsRow;
    private LinearLayout searchHistoryPanel;
    private LinearLayout searchHistoryRow;
    private int desktopMode = 20;
    private int pageAnimation = PAGE_ANIM_DEFAULT;
    private int sortMode = SORT_CUSTOM;
    private int desktopTheme = THEME_CLASSIC;
    private int iconStyle = ICON_STYLE_ORIGINAL;
    private int iconSize = ICON_SIZE_STANDARD;
    private int labelSize = LABEL_SIZE_STANDARD;
    private int currentDesktopPage = 0;
    private int selectedEditPosition = -1;
    private boolean hideDesktopLabels;
    private boolean lockDesktopLayout;
    private boolean loopDesktopPages;
    private boolean hapticFeedbackEnabled;
    private boolean fastLaunchEnabled;
    private boolean transparentThemeEnabled;
    private boolean editMode;
    private boolean viewTransitionRunning;
    private int viewTransitionToken;
    private boolean packageReceiverRegistered;
    private boolean packageListDirty;
    private float downY;
    private float downX;
    private float allAppsDownY;
    private float allAppsDownX;
    private long downTime;
    private float initialPointerDistance;
    private boolean pinchHandled;
    private VelocityTracker velocityTracker;
    private boolean pageAnimationRunning;
    private int pageAnimationToken;
    private AnimatorSet desktopPageAnimator;
    private boolean allAppsAddMode;
    private boolean allAppsPullHomeCandidate;
    private boolean allAppsPullHomeActive;
    private boolean showingManagementSettings;
    private boolean showingThemeSettings;
    private int settingsPage = SETTINGS_PAGE_MAIN;
    private int settingsParentPage = SETTINGS_PAGE_MAIN;
    private int activeAlphabetIndex = -1;
    private String lastAlphabetScrollLetter = "";
    private boolean resumedOnce;
    private boolean appsLoaded;
    private boolean appsLoading;
    private int appLoadToken;
    private volatile int texturePreloadToken;
    private volatile boolean activityDestroyed;
    private boolean desktopLoadingDismissed = true;
    private boolean pendingDesktopIconRefresh;
    private boolean pendingSearchIconRefresh;
    private Bitmap frostedWallpaperBitmap;
    private final Runnable packageRefreshRunnable = this::refreshAppsForCurrentSurface;
    private final Runnable desktopLoadingDismissRunnable = () -> {
        desktopLoadingDismissed = true;
        updateDesktopLoadingState();
    };
    private final Runnable alphabetIndexResetRunnable = () -> setActiveAlphabetIndex(-1, true);
    private final Runnable iconRefreshRunnable = () -> {
        if (activityDestroyed) {
            return;
        }
        boolean refreshDesktop = pendingDesktopIconRefresh;
        boolean refreshSearch = pendingSearchIconRefresh;
        pendingDesktopIconRefresh = false;
        pendingSearchIconRefresh = false;
        if (refreshDesktop && desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        if (refreshSearch
                && searchAdapter != null
                && searchView != null
                && searchView.getVisibility() == View.VISIBLE) {
            searchAdapter.notifyDataSetChanged();
        }
    };
    private final Runnable loadingAnimationRunnable = new Runnable() {
        private long startedAt;

        @Override
        public void run() {
            if (desktopLoadingOverlay == null
                    || desktopLoadingIcon == null
                    || desktopLoadingOverlay.getVisibility() != View.VISIBLE) {
                startedAt = 0L;
                return;
            }
            long now = System.currentTimeMillis();
            if (startedAt == 0L) {
                startedAt = now;
            }
            float phase = ((now - startedAt) % 1440L) / 1440f;
            if (desktopLoadingDrawable != null) {
                desktopLoadingDrawable.setPhase(phase);
            }
            float breath = 0.98f + 0.02f * (float) Math.sin(phase * Math.PI * 2f);
            desktopLoadingIcon.setScaleX(breath);
            desktopLoadingIcon.setScaleY(breath);
            desktopLoadingIcon.setAlpha(0.92f);
            mainHandler.postDelayed(this, 16L);
        }
    };
    private final BroadcastReceiver packageChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            packageListDirty = true;
            appsLoaded = false;
            clearAppCaches();
            mainHandler.removeCallbacks(packageRefreshRunnable);
            mainHandler.postDelayed(packageRefreshRunnable, 350L);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDestroyed = false;
        preferences = getSharedPreferences(PREFS, MODE_PRIVATE);
        migrateDesktopVisualDefaults();
        refreshPreferenceKeyCaches();
        int savedMode = preferences.contains(PREF_LAUNCHER_MODE)
                ? preferences.getInt(PREF_LAUNCHER_MODE, 20)
                : preferences.getInt("grid_columns", 20);
        desktopMode = normalizeDesktopMode(savedMode);
        preferences.edit().putInt(PREF_LAUNCHER_MODE, desktopMode).apply();
        pageAnimation = normalizePageAnimation(preferences.getInt(PREF_PAGE_ANIMATION, PAGE_ANIM_DEFAULT));
        sortMode = normalizeSortMode(preferences.getInt(PREF_SORT_MODE, SORT_CUSTOM));
        desktopTheme = normalizeDesktopTheme(preferences.getInt(PREF_DESKTOP_THEME, THEME_CLASSIC));
        iconStyle = normalizeIconStyle(preferences.getInt(PREF_ICON_STYLE, ICON_STYLE_ORIGINAL));
        iconSize = normalizeIconSize(preferences.getInt(PREF_ICON_SIZE, ICON_SIZE_STANDARD));
        labelSize = normalizeLabelSize(preferences.getInt(PREF_LABEL_SIZE, LABEL_SIZE_STANDARD));
        hideDesktopLabels = preferences.getBoolean(PREF_HIDE_LABELS, false);
        lockDesktopLayout = preferences.getBoolean(PREF_LOCK_LAYOUT, false);
        loopDesktopPages = preferences.getBoolean(PREF_LOOP_PAGES, false);
        hapticFeedbackEnabled = preferences.getBoolean(PREF_HAPTIC_FEEDBACK, true);
        fastLaunchEnabled = preferences.getBoolean(PREF_FAST_LAUNCH, false);
        transparentThemeEnabled = preferences.getBoolean(PREF_TRANSPARENT_THEME, false);
        configureSystemBars(false);
        setContentView(createContentView());
        registerPackageChangeReceiver();
        preloadCurrentThemeTextures();
        loadHomeApps();
        if (getIntent().getBooleanExtra(EXTRA_SHOW_SETTINGS, false)) {
            root.post(this::showSettings);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent != null && intent.getBooleanExtra(EXTRA_SHOW_SETTINGS, false)) {
            root.post(this::showSettings);
        }
    }

    private void migrateDesktopVisualDefaults() {
        int visualRevision = preferences.getInt(PREF_VISUAL_REVISION, 0);
        if (visualRevision >= CURRENT_VISUAL_REVISION) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        if (visualRevision < 2) {
            editor.putInt(PREF_LAUNCHER_MODE, 20)
                    .putInt(PREF_ICON_STYLE, ICON_STYLE_ORIGINAL)
                    .putInt(PREF_ICON_SIZE, ICON_SIZE_STANDARD)
                    .putInt(PREF_LABEL_SIZE, LABEL_SIZE_STANDARD);
        }
        if (visualRevision < 3) {
            int savedTheme = preferences.getInt(PREF_DESKTOP_THEME, THEME_CLASSIC);
            int smartisanIndex = savedTheme - THEME_SMARTISAN_LEGACY_START;
            if (smartisanIndex >= 0 && smartisanIndex < SMARTISAN_THEMES.length) {
                editor.putInt(PREF_DESKTOP_THEME, THEME_SMARTISAN_START + smartisanIndex);
            }
        }
        editor.putInt(PREF_VISUAL_REVISION, CURRENT_VISUAL_REVISION).apply();
    }

    @Override
    protected void onDestroy() {
        activityDestroyed = true;
        unregisterPackageChangeReceiver();
        mainHandler.removeCallbacksAndMessages(null);
        recycleFrostedWallpaper();
        appExecutor.shutdownNow();
        iconExecutor.shutdownNow();
        textureExecutor.shutdownNow();
        super.onDestroy();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level >= ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            texturePreloadToken++;
            ThemeTextureDrawable.clearTextureCache();
            sampledTextureColorCache.clear();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        texturePreloadToken++;
        ThemeTextureDrawable.clearTextureCache();
        sampledTextureColorCache.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (transparentThemeEnabled) {
            recycleFrostedWallpaper();
            applyDesktopTheme();
        }
        if (resumedOnce) {
            if (packageListDirty) {
                refreshAppsForCurrentSurface();
            } else {
                updateDesktopQuickLaunch();
            }
        } else {
            resumedOnce = true;
        }
    }

    private void registerPackageChangeReceiver() {
        if (packageReceiverRegistered) {
            return;
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        filter.addDataScheme("package");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(packageChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(packageChangeReceiver, filter);
        }
        packageReceiverRegistered = true;
    }

    private void unregisterPackageChangeReceiver() {
        if (!packageReceiverRegistered) {
            return;
        }
        try {
            unregisterReceiver(packageChangeReceiver);
        } catch (IllegalArgumentException ignored) {
            // Receiver may already be gone if the system tears down the Activity abruptly.
        }
        packageReceiverRegistered = false;
    }

    private void refreshPreferenceKeyCaches() {
        desktopAppKeySet.clear();
        desktopAppKeySet.addAll(parsePreferenceKeys(PREF_DESKTOP_APPS, 0));
        for (AppEntry app : desktopApps) {
            if (!TextUtils.isEmpty(app.key)) {
                desktopAppKeySet.add(app.key);
            }
        }

        hiddenAppKeySet.clear();
        hiddenAppKeySet.addAll(parsePreferenceKeys(PREF_HIDDEN_APPS, 0));

        pinnedQuickLaunchKeySet.clear();
        pinnedQuickLaunchKeySet.addAll(parsePreferenceKeys(PREF_PINNED_QUICK_LAUNCH, 4));
    }

    private List<String> parsePreferenceKeys(String preferenceKey, int limit) {
        String saved = preferences == null ? "" : preferences.getString(preferenceKey, "");
        List<String> keys = new ArrayList<>();
        if (TextUtils.isEmpty(saved)) {
            return keys;
        }
        Set<String> used = new HashSet<>();
        for (String key : saved.split("\\n")) {
            if (!TextUtils.isEmpty(key) && used.add(key)) {
                keys.add(key);
            }
            if (limit > 0 && keys.size() >= limit) {
                break;
            }
        }
        return keys;
    }

    private void clearAppCaches() {
        appEntryCache.clear();
        firstInstallTimeCache.clear();
    }

    @Override
    public void onBackPressed() {
        if (editMode && desktopView.getVisibility() == View.VISIBLE) {
            exitEditMode();
            return;
        }
        if (settingsView != null && settingsView.getVisibility() == View.VISIBLE
                && settingsPage != SETTINGS_PAGE_MAIN) {
            navigateSettingsPage(settingsParentPage, SETTINGS_PAGE_MAIN);
            refreshSettingsView();
            return;
        }
        if ((searchView != null && searchView.getVisibility() == View.VISIBLE)
                || (settingsView != null && settingsView.getVisibility() == View.VISIBLE)) {
            if (searchView != null && searchView.getVisibility() == View.VISIBLE) {
                setAllAppsAddMode(false);
            }
            showDesktop();
            return;
        }
        super.onBackPressed();
    }

    private View createContentView() {
        root = new FrameLayout(this);
        root.setBackground(createDesktopBackgroundDrawable());
        desktopView = createDesktopView();
        root.addView(desktopView);
        return root;
    }

    private View createDesktopView() {
        LinearLayout page = new LinearLayout(this);
        desktopPage = page;
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackground(createDesktopBackgroundDrawable());
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            applyDesktopWindowInsets(insets);
            return insets;
        });

        desktopTopBar = new LinearLayout(this);
        desktopTopBar.setBackgroundColor(getDesktopTopBarColor());
        page.addView(desktopTopBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0
        ));

        desktopStatus = new TextView(this);
        desktopStatus.setText(R.string.loading_apps);
        desktopStatus.setVisibility(View.GONE);

        desktopAdapter = new DesktopAdapter();
        desktopGrid = new GridView(this);
        desktopGrid.setNumColumns(getDesktopColumns());
        desktopGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        desktopGrid.setVerticalSpacing(0);
        desktopGrid.setHorizontalSpacing(0);
        desktopGrid.setSelector(android.R.color.transparent);
        desktopGrid.setCacheColorHint(Color.TRANSPARENT);
        desktopGrid.setBackgroundColor(Color.TRANSPARENT);
        desktopGrid.setVerticalScrollBarEnabled(false);
        desktopGrid.setOverScrollMode(View.OVER_SCROLL_NEVER);
        desktopGrid.setGravity(Gravity.CENTER);
        desktopGrid.setClipToPadding(false);
        desktopGrid.setFadingEdgeLength(0);
        desktopGrid.setScrollingCacheEnabled(false);
        desktopGrid.setAnimationCacheEnabled(false);
        desktopGrid.setAlwaysDrawnWithCacheEnabled(false);
        desktopGrid.setPersistentDrawingCache(ViewGroup.PERSISTENT_NO_CACHE);
        desktopGrid.setDrawingCacheEnabled(false);
        desktopGrid.setWillNotCacheDrawing(true);
        desktopGrid.setAdapter(desktopAdapter);
        updateDesktopGridInsets();
        desktopGrid.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int oldHeight = oldBottom - oldTop;
            int newHeight = bottom - top;
            if (newHeight > 0 && newHeight != oldHeight && desktopAdapter != null) {
                desktopAdapter.notifyDataSetChanged();
            }
        });
        desktopGrid.setOnItemClickListener((parent, view, position, id) -> {
            if (editMode) {
                handleEditTap(position);
                return;
            }
            AppEntry app = getDesktopApp(position);
            if (app != null) {
                openApp(app);
            }
        });
        desktopGrid.setOnItemLongClickListener((parent, view, position, id) -> {
            AppEntry app = getDesktopApp(position);
            if (editMode) {
                if (app != null) {
                    return startDesktopDrag(view, position);
                }
                return false;
            }
            if (app != null) {
                if (!lockDesktopLayout) {
                    enterEditMode();
                    return startDesktopDrag(view, position);
                }
                showAppActions(app);
                return true;
            }
            return false;
        });
        desktopGrid.setOnTouchListener((view, event) -> {
            handleDesktopGesture(event);
            return false;
        });
        desktopGridLayer = new FrameLayout(this);
        desktopGridLayer.setClipChildren(false);
        desktopGridLayer.setClipToPadding(false);
        desktopGridLayer.setBackgroundColor(Color.TRANSPARENT);
        desktopGridLayer.addView(desktopGrid, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        desktopGridLayer.addView(createDesktopLoadingOverlay(), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        page.addView(desktopGridLayer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        pageIndicator = new LinearLayout(this);
        pageIndicator.setOrientation(LinearLayout.HORIZONTAL);
        pageIndicator.setGravity(Gravity.CENTER);
        pageIndicator.setPadding(0, dp(3), 0, dp(5));
        pageIndicator.setBackground(createDesktopBackgroundDrawable());
        pageIndicator.setVisibility(View.GONE);
        page.addView(pageIndicator, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(26)
        ));

        desktopDock = new LinearLayout(this);
        desktopDock.setOrientation(LinearLayout.VERTICAL);
        desktopDock.setGravity(Gravity.CENTER);
        desktopDock.setPadding(0, 0, 0, 0);
        updateDockBackground();
        desktopDock.setOnTouchListener((view, event) -> {
            return handleDockGesture(event);
        });
        desktopDock.setOnDragListener((view, event) -> handleDockDropEvent(event));
        page.addView(desktopDock, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(128)
        ));
        page.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) ->
                updateDesktopLayoutForWindow());

        desktopHint = new TextView(this);
        desktopHint.setText(R.string.swipe_search_hint);
        desktopHint.setVisibility(View.GONE);
        desktopDock.addView(createDesktopQuickLaunchPanel(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        updateDesktopQuickLaunch();
        page.post(page::requestApplyInsets);

        return page;
    }

    private View createManagementSettingsView() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackground(new SettingsPageBackgroundDrawable());

        LinearLayout titleBar = new LinearLayout(this);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(4), dp(6), dp(4), 0);
        titleBar.setBackground(new SettingsHeaderDrawable());
        page.addView(titleBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        ));
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets == null ? 0 : insets.getSystemWindowInsetTop();
            int targetHeight = dp(60) + topInset;
            titleBar.setPadding(dp(4), topInset, dp(4), 0);
            ViewGroup.LayoutParams params = titleBar.getLayoutParams();
            if (params != null && params.height != targetHeight) {
                params.height = targetHeight;
                titleBar.setLayoutParams(params);
            }
            return insets;
        });

        ImageView back = new ImageView(this);
        back.setImageDrawable(new BackGlyphDrawable(Color.rgb(74, 80, 86)));
        back.setScaleType(ImageView.ScaleType.CENTER);
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(v -> {
            navigateSettingsPage(SETTINGS_PAGE_MAIN, SETTINGS_PAGE_MAIN);
            refreshSettingsView();
        });
        FrameLayout backSlot = new FrameLayout(this);
        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(dp(52), dp(52), Gravity.CENTER_VERTICAL);
        backParams.setMargins(dp(20), 0, 0, 0);
        backSlot.addView(back, backParams);
        titleBar.addView(backSlot, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText(R.string.obsession_options);
        title.setTextColor(Color.rgb(82, 86, 90));
        title.setTextSize(21);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine(true);
        titleBar.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView spacer = new TextView(this);
        titleBar.addView(spacer, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        page.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(28), dp(16), dp(32));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        content.addView(createSettingsSectionTitle(R.string.management_layout_section));
        LinearLayout layoutRows = createSettingsCard();
        addSettingsRow(layoutRows, createSimpleSettingsRow(R.string.icon_size, getIconSizeName(),
                true, createSettingsIcon("icon"), v -> showIconSizeChooser()));
        addSettingsRow(layoutRows, createSimpleSettingsRow(R.string.label_size, getLabelSizeName(),
                true, createSettingsIcon("icon"), v -> showLabelSizeChooser()));
        addSettingsRow(layoutRows, createSimpleSettingsRow(R.string.app_sort, getSortModeName(),
                true, createSettingsIcon("manage"), v -> showSortModeChooser()));
        content.addView(layoutRows);
        addVerticalSpace(content, dp(22));

        content.addView(createSettingsSectionTitle(R.string.management_visibility_section));
        LinearLayout visibilityRows = createSettingsCard();
        addSettingsRow(visibilityRows, createSimpleSettingsRow(R.string.hidden_apps, getHiddenAppsSummary(),
                true, createSettingsIcon("manage"), v -> showHiddenAppsChooser()));
        addSettingsRow(visibilityRows, createSimpleSettingsRow(R.string.quick_launch_manage, getQuickLaunchPinnedSummary(),
                true, createSettingsIcon("icon"), v -> showQuickLaunchPinnedChooser()));
        addSettingsRow(visibilityRows, createManagementSwitchRow(R.string.hide_desktop_labels,
                hideDesktopLabels, checked -> {
                    hideDesktopLabels = checked;
                    preferences.edit().putBoolean(PREF_HIDE_LABELS, hideDesktopLabels).apply();
                    if (desktopAdapter != null) {
                        desktopAdapter.notifyDataSetChanged();
                    }
                }));
        content.addView(visibilityRows);
        addVerticalSpace(content, dp(22));

        content.addView(createSettingsSectionTitle(R.string.management_interaction_section));
        LinearLayout interactionRows = createSettingsCard();
        addSettingsRow(interactionRows, createManagementSwitchRow(R.string.lock_desktop_layout,
                lockDesktopLayout, checked -> {
                    lockDesktopLayout = checked;
                    preferences.edit().putBoolean(PREF_LOCK_LAYOUT, lockDesktopLayout).apply();
                    if (lockDesktopLayout) {
                        exitEditMode();
                    }
                }));
        addSettingsRow(interactionRows, createManagementSwitchRow(R.string.loop_desktop_pages,
                loopDesktopPages, checked -> {
                    loopDesktopPages = checked;
                    preferences.edit().putBoolean(PREF_LOOP_PAGES, loopDesktopPages).apply();
                }));
        addSettingsRow(interactionRows, createManagementSwitchRow(R.string.haptic_feedback,
                hapticFeedbackEnabled, checked -> {
                    hapticFeedbackEnabled = checked;
                    preferences.edit().putBoolean(PREF_HAPTIC_FEEDBACK, hapticFeedbackEnabled).apply();
                }));
        content.addView(interactionRows);
        addVerticalSpace(content, dp(22));

        content.addView(createSettingsSectionTitle(R.string.management_maintenance_section));
        LinearLayout maintenanceRows = createSettingsCard();
        addSettingsRow(maintenanceRows, createSimpleSettingsRow(R.string.usage_records,
                getString(R.string.usage_records_summary), true, createSettingsIcon("manage"),
                v -> showUsageRecordsCleaner()));
        addSettingsRow(maintenanceRows, createSimpleSettingsRow(R.string.reset_desktop_layout,
                getString(R.string.reset_desktop_layout_summary), true, createSettingsIcon("manage"),
                v -> confirmResetDesktopLayout()));
        addSettingsRow(maintenanceRows, createSimpleSettingsRow(R.string.refresh_list,
                getString(R.string.refresh_apps_summary), true, createSettingsIcon("version"),
                v -> refreshAppList()));
        content.addView(maintenanceRows);
        page.post(page::requestApplyInsets);
        return page;
    }

    private View createThemeSettingsView() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackground(new SettingsPageBackgroundDrawable());

        LinearLayout titleBar = new LinearLayout(this);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(4), dp(6), dp(4), 0);
        titleBar.setBackground(new SettingsHeaderDrawable());
        page.addView(titleBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        ));
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets == null ? 0 : insets.getSystemWindowInsetTop();
            int targetHeight = dp(60) + topInset;
            titleBar.setPadding(dp(4), topInset, dp(4), 0);
            ViewGroup.LayoutParams params = titleBar.getLayoutParams();
            if (params != null && params.height != targetHeight) {
                params.height = targetHeight;
                titleBar.setLayoutParams(params);
            }
            return insets;
        });

        ImageView back = new ImageView(this);
        back.setImageDrawable(new BackGlyphDrawable(Color.rgb(74, 80, 86)));
        back.setScaleType(ImageView.ScaleType.CENTER);
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(v -> {
            navigateSettingsPage(SETTINGS_PAGE_MAIN, SETTINGS_PAGE_MAIN);
            refreshSettingsView();
        });
        FrameLayout backSlot = new FrameLayout(this);
        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(dp(52), dp(52), Gravity.CENTER_VERTICAL);
        backParams.setMargins(dp(20), 0, 0, 0);
        backSlot.addView(back, backParams);
        titleBar.addView(backSlot, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText(R.string.desktop_theme);
        title.setTextColor(Color.rgb(82, 86, 90));
        title.setTextSize(21);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine(true);
        titleBar.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView spacer = new TextView(this);
        titleBar.addView(spacer, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        page.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(28), dp(16), dp(32));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        content.addView(createCurrentThemeCard());
        addVerticalSpace(content, dp(24));
        content.addView(createSettingsSectionTitle(R.string.theme_builtin_section));
        content.addView(createThemeChoiceGrid(getBaseThemeValues()));
        addVerticalSpace(content, dp(24));
        content.addView(createSettingsSectionTitle(R.string.theme_smartisan_section));
        content.addView(createThemeChoiceGrid(getSmartisanThemeValues()));
        page.post(page::requestApplyInsets);
        return page;
    }

    private View createCurrentThemeCard() {
        LinearLayout card = createSettingsCard();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(18), dp(16), dp(20), dp(16));

        ImageView preview = new ImageView(this);
        preview.setImageDrawable(new ThemePreviewDrawable(getThemePreviewPalette(desktopTheme), true));
        preview.setScaleType(ImageView.ScaleType.FIT_XY);
        card.addView(preview, new LinearLayout.LayoutParams(dp(96), dp(120)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dp(20), 0, 0, 0);
        card.addView(texts, textParams);

        TextView label = new TextView(this);
        label.setText(R.string.theme_current);
        label.setTextColor(Color.rgb(126, 130, 134));
        label.setTextSize(15);
        label.setSingleLine(true);
        texts.addView(label);

        TextView name = new TextView(this);
        name.setText(getDesktopThemeName());
        name.setTextColor(Color.rgb(52, 56, 60));
        name.setTextSize(24);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        name.setSingleLine(true);
        name.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        nameParams.setMargins(0, dp(6), 0, 0);
        texts.addView(name, nameParams);

        TextView subtitle = new TextView(this);
        subtitle.setText(R.string.desktop_theme_subtitle);
        subtitle.setTextColor(Color.rgb(128, 132, 136));
        subtitle.setTextSize(15);
        subtitle.setSingleLine(false);
        subtitle.setMaxLines(2);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        subtitleParams.setMargins(0, dp(8), 0, 0);
        texts.addView(subtitle, subtitleParams);
        return card;
    }

    private View createThemeChoiceGrid(int[] values) {
        LinearLayout grid = new LinearLayout(this);
        grid.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < values.length; i += 2) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                rowParams.setMargins(0, dp(12), 0, 0);
            }
            grid.addView(row, rowParams);

            View left = createThemeChoiceTile(values[i]);
            LinearLayout.LayoutParams leftParams = new LinearLayout.LayoutParams(0, dp(156), 1f);
            leftParams.setMargins(0, 0, dp(6), 0);
            row.addView(left, leftParams);

            if (i + 1 < values.length) {
                View right = createThemeChoiceTile(values[i + 1]);
                LinearLayout.LayoutParams rightParams = new LinearLayout.LayoutParams(0, dp(156), 1f);
                rightParams.setMargins(dp(6), 0, 0, 0);
                row.addView(right, rightParams);
            } else {
                View spacer = new View(this);
                LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(0, dp(156), 1f);
                spacerParams.setMargins(dp(6), 0, 0, 0);
                row.addView(spacer, spacerParams);
            }
        }
        return grid;
    }

    private View createThemeChoiceTile(int themeValue) {
        boolean selected = desktopTheme == themeValue;
        LinearLayout tile = new LinearLayout(this);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER_HORIZONTAL);
        tile.setPadding(dp(10), dp(10), dp(10), dp(10));
        tile.setBackground(new ThemeChoiceCardDrawable(selected));
        tile.setClickable(true);
        tile.setFocusable(true);
        tile.setElevation(dp(1));
        tile.setOnClickListener(v -> selectDesktopTheme(themeValue));

        FrameLayout previewWrap = new FrameLayout(this);
        tile.addView(previewWrap, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        ImageView preview = new ImageView(this);
        preview.setImageDrawable(new ThemePreviewDrawable(getThemePreviewPalette(themeValue), selected));
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FrameLayout.LayoutParams previewParams = new FrameLayout.LayoutParams(
                dp(118),
                dp(90),
                Gravity.CENTER
        );
        previewWrap.addView(preview, previewParams);

        if (selected) {
            View check = new View(this);
            check.setBackground(new SettingsCheckDrawable());
            FrameLayout.LayoutParams checkParams = new FrameLayout.LayoutParams(dp(32), dp(32), Gravity.TOP | Gravity.RIGHT);
            checkParams.setMargins(0, dp(2), dp(4), 0);
            previewWrap.addView(check, checkParams);
        }

        TextView label = new TextView(this);
        label.setText(getDesktopThemeLabelText(themeValue));
        label.setTextColor(selected ? Color.rgb(185, 76, 68) : Color.rgb(59, 63, 67));
        label.setTextSize(15);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(false);
        label.setMaxLines(2);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dp(8), 0, 0);
        tile.addView(label, labelParams);
        return tile;
    }

    private int[] getBaseThemeValues() {
        int[] values = new int[PANTONE_THEMES.length];
        for (int i = 0; i < PANTONE_THEMES.length; i++) {
            values[i] = THEME_PANTONE_START + i;
        }
        return values;
    }

    private int[] getSmartisanThemeValues() {
        int[] values = new int[SMARTISAN_THEMES.length];
        for (int i = 0; i < SMARTISAN_THEMES.length; i++) {
            values[i] = THEME_SMARTISAN_START + i;
        }
        return values;
    }

    private CharSequence getDesktopThemeLabelText(int value) {
        SmartisanTheme smartisanTheme = getSmartisanTheme(value);
        if (smartisanTheme != null) {
            return getText(smartisanTheme.labelResId);
        }
        PantoneTheme pantoneTheme = getPantoneTheme(value);
        if (pantoneTheme != null) {
            return pantoneTheme.getLabel();
        }
        switch (value) {
            case THEME_GRAPHITE:
                return getText(R.string.theme_graphite);
            case THEME_COPPER:
                return getText(R.string.theme_copper);
            case THEME_ORIGINAL_BLUE:
                return getText(R.string.theme_original_blue);
            case THEME_CLASSIC:
            default:
                return getText(R.string.theme_classic);
        }
    }

    private void selectDesktopTheme(int themeValue) {
        if (desktopTheme == themeValue) {
            return;
        }
        desktopTheme = themeValue;
        preferences.edit().putInt(PREF_DESKTOP_THEME, desktopTheme).apply();
        applyDesktopTheme();
        refreshSettingsView();
    }

    private ThemePalette getThemePreviewPalette(int value) {
        ThemePalette pantonePalette = getPantonePalette(value);
        if (pantonePalette != null) {
            return pantonePalette;
        }
        switch (value) {
            case THEME_GRAPHITE:
                return new ThemePalette(Color.rgb(48, 54, 59), Color.rgb(30, 35, 39),
                        Color.rgb(98, 106, 112), Color.rgb(32, 37, 42), Color.rgb(157, 166, 174), false);
            case THEME_COPPER:
                return new ThemePalette(Color.rgb(83, 58, 47), Color.rgb(42, 34, 32),
                        Color.rgb(149, 105, 82), Color.rgb(47, 37, 34), Color.rgb(199, 126, 88), false);
            case THEME_ORIGINAL_BLUE:
                return new ThemePalette(Color.rgb(34, 66, 96), Color.rgb(18, 35, 56),
                        Color.rgb(81, 130, 178), Color.rgb(18, 32, 49), Color.rgb(96, 155, 213), false);
            case THEME_CLASSIC:
                return new ThemePalette(Color.rgb(48, 53, 57), Color.rgb(33, 38, 42),
                        Color.rgb(86, 92, 97), Color.rgb(29, 34, 38), Color.rgb(204, 207, 209), false);
            default:
                SmartisanTheme smartisanTheme = getSmartisanTheme(value);
                if (smartisanTheme == null) {
                    return getThemePreviewPalette(THEME_CLASSIC);
                }
                return getSmartisanTexturePalette(smartisanTheme);
        }
    }

    private ThemePalette getSmartisanTexturePalette(SmartisanTheme smartisanTheme) {
        ThemePalette fallback = getSmartisanFallbackPalette(smartisanTheme);
        int top = getSmartisanTextureColor(smartisanTheme, 0, TEXTURE_SAMPLE_TOP, fallback.topColor);
        int bottom = getSmartisanTextureColor(smartisanTheme, 0, TEXTURE_SAMPLE_BOTTOM, fallback.bottomColor);
        int cell = getSmartisanTextureColor(smartisanTheme, 1, TEXTURE_SAMPLE_FULL, fallback.cellColor);
        int dock = getSmartisanDockTextureColor(smartisanTheme, fallback.dockColor);
        int accent = mixColor(cell, Color.WHITE, isLightColor(cell) ? 0.08f : 0.24f);
        return new ThemePalette(top, bottom, cell, dock, accent, isLightColor(top) || isLightColor(cell));
    }

    private ThemePalette getSmartisanFallbackPalette(SmartisanTheme smartisanTheme) {
        String dir = smartisanTheme.assetDir.toLowerCase(Locale.US);
        if (dir.contains("aero") || dir.contains("literarywhite")) {
            return new ThemePalette(Color.rgb(222, 230, 234), Color.rgb(131, 144, 153),
                    Color.rgb(245, 248, 249), Color.rgb(106, 119, 127), Color.rgb(202, 214, 220), true);
        }
        if (dir.contains("pink")) {
            return new ThemePalette(Color.rgb(221, 139, 158), Color.rgb(118, 63, 83),
                    Color.rgb(246, 190, 203), Color.rgb(105, 52, 70), Color.rgb(238, 149, 169), false);
        }
        if (dir.contains("purple")) {
            return new ThemePalette(Color.rgb(105, 79, 142), Color.rgb(48, 39, 74),
                    Color.rgb(151, 126, 188), Color.rgb(42, 33, 65), Color.rgb(173, 139, 214), false);
        }
        if (dir.contains("red") || dir.contains("winered")) {
            return new ThemePalette(Color.rgb(135, 46, 48), Color.rgb(58, 26, 31),
                    Color.rgb(187, 80, 78), Color.rgb(50, 24, 28), Color.rgb(224, 89, 85), false);
        }
        if (dir.contains("orange") || dir.contains("yellow") || dir.contains("lightgold")) {
            return new ThemePalette(Color.rgb(196, 139, 63), Color.rgb(88, 58, 28),
                    Color.rgb(236, 189, 94), Color.rgb(78, 50, 24), Color.rgb(244, 174, 65), false);
        }
        if (dir.contains("green") || dir.contains("leaf") || dir.contains("bamboo")
                || dir.contains("lake") || dir.contains("cyan") || dir.contains("bluegreen")) {
            return new ThemePalette(Color.rgb(54, 122, 103), Color.rgb(25, 61, 58),
                    Color.rgb(90, 173, 146), Color.rgb(22, 53, 50), Color.rgb(94, 196, 163), false);
        }
        if (dir.contains("wood") || dir.contains("leather") || dir.contains("clay")
                || dir.contains("brown")) {
            return new ThemePalette(Color.rgb(103, 70, 47), Color.rgb(45, 32, 27),
                    Color.rgb(157, 112, 77), Color.rgb(42, 29, 24), Color.rgb(193, 128, 80), false);
        }
        if (dir.contains("gray") || dir.contains("grid") || dir.contains("strip")
                || dir.contains("fibre")) {
            return new ThemePalette(Color.rgb(83, 91, 96), Color.rgb(38, 43, 47),
                    Color.rgb(136, 145, 151), Color.rgb(34, 39, 43), Color.rgb(172, 184, 190), false);
        }
        return new ThemePalette(Color.rgb(38, 76, 112), Color.rgb(18, 35, 59),
                Color.rgb(78, 138, 192), Color.rgb(16, 29, 46), Color.rgb(89, 158, 222), false);
    }

    private int getSmartisanTextureColor(SmartisanTheme theme, int position, int sampleArea, int fallback) {
        return getTextureSampleColor(getSmartisanThemeTexturePath(theme, position), sampleArea, fallback);
    }

    private int getSmartisanDockTextureColor(SmartisanTheme theme, int fallback) {
        return getTextureSampleColor(getSmartisanThemeTexturePath(theme, -1), TEXTURE_SAMPLE_FULL, fallback);
    }

    private int getTextureSampleColor(String assetPath, int sampleArea, int fallback) {
        String key = assetPath + "#" + sampleArea;
        Integer cached = sampledTextureColorCache.get(key);
        if (cached != null) {
            return cached;
        }
        Bitmap bitmap = ThemeTextureDrawable.getBitmap(getResources(), assetPath);
        int color = sampleTextureColor(bitmap, sampleArea, fallback);
        sampledTextureColorCache.put(key, color);
        return color;
    }

    private static int sampleTextureColor(Bitmap bitmap, int sampleArea, int fallback) {
        if (bitmap == null || bitmap.isRecycled() || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return fallback;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int startY = 0;
        int endY = height;
        if (sampleArea == TEXTURE_SAMPLE_TOP) {
            endY = Math.max(1, height / 5);
        } else if (sampleArea == TEXTURE_SAMPLE_BOTTOM) {
            startY = Math.max(0, height - Math.max(1, height / 5));
        }
        int stepX = Math.max(1, width / 28);
        int stepY = Math.max(1, Math.max(1, endY - startY) / 28);
        long red = 0L;
        long green = 0L;
        long blue = 0L;
        long alpha = 0L;
        for (int y = startY; y < endY; y += stepY) {
            for (int x = 0; x < width; x += stepX) {
                int color = bitmap.getPixel(x, y);
                int a = Color.alpha(color);
                if (a < 32) {
                    continue;
                }
                red += (long) Color.red(color) * a;
                green += (long) Color.green(color) * a;
                blue += (long) Color.blue(color) * a;
                alpha += a;
            }
        }
        if (alpha <= 0L) {
            return fallback;
        }
        return Color.rgb((int) (red / alpha), (int) (green / alpha), (int) (blue / alpha));
    }

    private View createDesktopLoadingOverlay() {
        desktopLoadingOverlay = new LinearLayout(this);
        desktopLoadingOverlay.setOrientation(LinearLayout.VERTICAL);
        desktopLoadingOverlay.setGravity(Gravity.CENTER);
        desktopLoadingOverlay.setPadding(dp(24), 0, dp(24), dp(18));
        desktopLoadingOverlay.setClickable(false);
        desktopLoadingOverlay.setFocusable(false);

        desktopLoadingIcon = new ImageView(this);
        desktopLoadingIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        desktopLoadingDrawable = new DesktopLoadingDrawable();
        desktopLoadingIcon.setImageDrawable(desktopLoadingDrawable);
        desktopLoadingOverlay.addView(desktopLoadingIcon, new LinearLayout.LayoutParams(dp(76), dp(76)));

        desktopLoadingText = new TextView(this);
        desktopLoadingText.setText(R.string.loading_home);
        desktopLoadingText.setTextColor(Color.rgb(178, 183, 187));
        desktopLoadingText.setTextSize(13);
        desktopLoadingText.setGravity(Gravity.CENTER);
        desktopLoadingText.setSingleLine(true);
        desktopLoadingText.setShadowLayer(2f, 0f, 2f, Color.argb(160, 0, 0, 0));
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textParams.setMargins(0, dp(12), 0, 0);
        desktopLoadingOverlay.addView(desktopLoadingText, textParams);
        updateDesktopLoadingState();
        return desktopLoadingOverlay;
    }

    private void applyDesktopWindowInsets(WindowInsets insets) {
        if (desktopTopBar == null || insets == null) {
            return;
        }
        desktopWindowInsets = insets;
        int topInset = insets.getSystemWindowInsetTop();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && insets.getDisplayCutout() != null) {
            topInset = Math.max(topInset, insets.getDisplayCutout().getSafeInsetTop());
        }
        int targetHeight = 0;
        if (topInset > 0) {
            int[] location = new int[2];
            desktopPage.getLocationOnScreen(location);
            boolean contentAlreadyBelowStatus = location[1] >= topInset - dp(2);
            if (!contentAlreadyBelowStatus) {
                targetHeight = Math.min(dp(58), topInset);
            }
        }
        ViewGroup.LayoutParams params = desktopTopBar.getLayoutParams();
        if (params != null && params.height != targetHeight) {
            params.height = targetHeight;
            desktopTopBar.setLayoutParams(params);
            updateDesktopDockHeight();
        }
    }

    private void updateDesktopLayoutForWindow() {
        if (desktopWindowInsets != null) {
            applyDesktopWindowInsets(desktopWindowInsets);
        }
        updateDesktopDockHeight();
    }

    private View createDesktopSettingsButton() {
        LinearLayout button = new LinearLayout(this);
        button.setOrientation(LinearLayout.VERTICAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(4), dp(7), dp(4), dp(4));
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnClickListener(v -> showSettings());
        button.setOnTouchListener((view, event) -> {
            handleDesktopGesture(event);
            return false;
        });

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(new SettingsGearDrawable());
        button.addView(icon, new LinearLayout.LayoutParams(dp(50), dp(50)));

        TextView label = new TextView(this);
        label.setText(R.string.desktop_settings_short);
        label.setTextColor(Color.rgb(172, 176, 179));
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setShadowLayer(2f, 0f, 2f, Color.argb(160, 0, 0, 0));
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dp(6), 0, 0);
        button.addView(label, labelParams);
        return button;
    }

    private View createDesktopQuickLaunchPanel() {
        desktopQuickLaunchPanel = new LinearLayout(this);
        desktopQuickLaunchPanel.setOrientation(LinearLayout.VERTICAL);
        desktopQuickLaunchPanel.setGravity(Gravity.CENTER);
        desktopQuickLaunchPanel.setPadding(dp(10), dp(5), dp(10), dp(6));

        desktopQuickLaunchRow = new LinearLayout(this);
        desktopQuickLaunchRow.setOrientation(LinearLayout.HORIZONTAL);
        desktopQuickLaunchRow.setGravity(Gravity.CENTER);
        desktopQuickLaunchPanel.addView(desktopQuickLaunchRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        return desktopQuickLaunchPanel;
    }

    private View createDesktopQuickLaunchButton(AppEntry app) {
        LinearLayout button = new LinearLayout(this);
        button.setOrientation(LinearLayout.VERTICAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(4), dp(7), dp(4), dp(4));
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnTouchListener((view, event) -> {
            handleDesktopGesture(event);
            return false;
        });
        button.setOnClickListener(v -> openApp(app));
        button.setOnLongClickListener(v -> {
            showAppActions(app);
            return true;
        });

        FrameLayout iconFrame = new FrameLayout(this);
        button.addView(iconFrame, new LinearLayout.LayoutParams(dp(50), dp(50)));

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(createDisplayIcon(app));
        int iconSize = getQuickLaunchIconSize();
        iconFrame.addView(icon, new FrameLayout.LayoutParams(iconSize, iconSize, Gravity.CENTER));

        if (isQuickLaunchPinned(app)) {
            View pinMark = new View(this);
            pinMark.setBackground(rounded(Color.rgb(205, 95, 82), dp(4), dp(1), Color.rgb(246, 210, 205)));
            iconFrame.addView(pinMark, new FrameLayout.LayoutParams(dp(9), dp(9), Gravity.TOP | Gravity.RIGHT));
        }

        TextView label = new TextView(this);
        label.setText(app.label);
        label.setTextColor(Color.rgb(184, 190, 194));
        label.setTextSize(getQuickLaunchLabelTextSize());
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dp(6), 0, 0);
        button.addView(label, labelParams);
        return button;
    }

    private View createDockEmptySlot() {
        View slot = new View(this);
        slot.setVisibility(View.INVISIBLE);
        slot.setOnTouchListener((view, event) -> {
            return handleDockGesture(event);
        });
        return slot;
    }

    private View createDockButton(int labelRes, int iconType, View.OnClickListener listener) {
        LinearLayout button = new LinearLayout(this);
        button.setOrientation(LinearLayout.VERTICAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(4), dp(8), dp(4), 0);
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnTouchListener((view, event) -> {
            return handleDockGesture(event);
        });
        button.setOnClickListener(listener);

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageResource(getDockIconResource(iconType));
        button.addView(icon, new LinearLayout.LayoutParams(dp(56), dp(56)));

        TextView label = new TextView(this);
        label.setText(labelRes);
        label.setTextColor(Color.rgb(190, 195, 199));
        label.setTextSize(12);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dp(5), 0, 0);
        button.addView(label, labelParams);
        return button;
    }

    private int getDockIconResource(int iconType) {
        if (iconType == DOCK_PHONE) {
            return R.drawable.dock_icon_phone;
        }
        if (iconType == DOCK_SETTINGS) {
            return R.drawable.dock_icon_settings;
        }
        return R.drawable.dock_icon_messages;
    }

    private View createSearchView() {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(0, dp(10), 0, 0);
        page.setBackgroundColor(Color.rgb(245, 245, 242));
        page.setFocusable(true);
        page.setFocusableInTouchMode(true);
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets == null ? 0 : insets.getSystemWindowInsetTop();
            view.setPadding(0, topInset + dp(8), 0, 0);
            return insets;
        });

        LinearLayout searchBar = new LinearLayout(this);
        searchBar.setOrientation(LinearLayout.HORIZONTAL);
        searchBar.setGravity(Gravity.CENTER_VERTICAL);
        searchBar.setPadding(dp(10), dp(10), dp(10), dp(10));
        page.addView(searchBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView back = new ImageView(this);
        back.setImageDrawable(new BackGlyphDrawable(Color.rgb(74, 80, 86)));
        back.setScaleType(ImageView.ScaleType.CENTER);
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(v -> showDesktop());
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(dp(52), dp(52));
        backParams.setMargins(0, 0, dp(8), 0);
        searchBar.addView(back, backParams);

        ImageView magnifier = new ImageView(this);
        magnifier.setImageDrawable(new SearchGlyphDrawable());
        magnifier.setScaleType(ImageView.ScaleType.CENTER);

        LinearLayout inputShell = new LinearLayout(this);
        inputShell.setOrientation(LinearLayout.HORIZONTAL);
        inputShell.setGravity(Gravity.CENTER_VERTICAL);
        inputShell.setPadding(dp(14), 0, dp(8), 0);
        inputShell.setBackground(rounded(Color.rgb(253, 253, 252), dp(26), dp(1), Color.rgb(222, 224, 226)));
        searchBar.addView(inputShell, new LinearLayout.LayoutParams(
                0,
                dp(52),
                1f
        ));
        LinearLayout.LayoutParams magnifierParams = new LinearLayout.LayoutParams(dp(32), dp(32));
        magnifierParams.setMargins(0, 0, dp(4), 0);
        inputShell.addView(magnifier, magnifierParams);

        searchBox = new EditText(this);
        searchBox.setSingleLine(true);
        searchBox.setTextSize(18);
        searchBox.setHint(R.string.search_hint_short);
        searchBox.setTextColor(Color.rgb(38, 42, 45));
        searchBox.setHintTextColor(Color.rgb(182, 185, 188));
        searchBox.setBackgroundColor(Color.TRANSPARENT);
        searchBox.setPadding(dp(4), 0, dp(4), 0);
        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterApps(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            boolean searchAction = actionId == EditorInfo.IME_ACTION_SEARCH;
            boolean enterAction = event != null
                    && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_UP;
            if (searchAction || enterAction) {
                launchFirstSearchResult();
                return true;
            }
            return false;
        });
        inputShell.addView(searchBox, new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        ));

        ImageView clear = new ImageView(this);
        clear.setImageDrawable(new ClearGlyphDrawable());
        clear.setScaleType(ImageView.ScaleType.FIT_CENTER);
        clear.setClickable(true);
        clear.setFocusable(true);
        clear.setOnClickListener(v -> searchBox.setText(""));
        inputShell.addView(clear, new LinearLayout.LayoutParams(dp(32), dp(32)));

        allAppsActionButton = new TextView(this);
        allAppsActionButton.setText(R.string.add_apps_action);
        allAppsActionButton.setTextSize(13);
        allAppsActionButton.setTypeface(Typeface.DEFAULT_BOLD);
        allAppsActionButton.setTextColor(Color.rgb(66, 72, 78));
        allAppsActionButton.setGravity(Gravity.CENTER);
        allAppsActionButton.setSingleLine(true);
        allAppsActionButton.setBackground(rounded(Color.rgb(253, 253, 252), dp(26), dp(1), Color.rgb(222, 224, 226)));
        allAppsActionButton.setOnClickListener(v -> {
            if (allAppsAddMode) {
                showBatchAddDialog();
            } else {
                setAllAppsAddMode(true);
            }
        });
        LinearLayout.LayoutParams batchParams = new LinearLayout.LayoutParams(dp(52), dp(52));
        batchParams.setMargins(dp(8), 0, 0, 0);
        searchBar.addView(allAppsActionButton, batchParams);

        LinearLayout sectionBar = new LinearLayout(this);
        sectionBar.setOrientation(LinearLayout.HORIZONTAL);
        sectionBar.setGravity(Gravity.CENTER_VERTICAL);
        sectionBar.setPadding(dp(20), dp(8), dp(16), dp(7));
        sectionBar.setBackgroundColor(Color.rgb(238, 239, 238));
        page.addView(sectionBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(48)
        ));

        allAppsTitle = new TextView(this);
        allAppsTitle.setText(R.string.all_apps_launcher_title);
        allAppsTitle.setTextSize(18);
        allAppsTitle.setTypeface(Typeface.DEFAULT_BOLD);
        allAppsTitle.setTextColor(Color.rgb(72, 77, 82));
        allAppsTitle.setSingleLine(true);
        sectionBar.addView(allAppsTitle, new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        ));

        searchStatus = new TextView(this);
        searchStatus.setText(R.string.apps_section);
        searchStatus.setTextSize(14);
        searchStatus.setTextColor(Color.rgb(132, 136, 140));
        searchStatus.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        searchStatus.setSingleLine(true);
        sectionBar.addView(searchStatus, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        searchAdapter = new AppAdapter();
        allAppsListView = new ListView(this);
        allAppsListView.setAdapter(searchAdapter);
        allAppsListView.setDividerHeight(dp(1));
        allAppsListView.setDivider(rounded(Color.rgb(232, 233, 234), 0, 0, Color.TRANSPARENT));
        allAppsListView.setCacheColorHint(Color.TRANSPARENT);
        allAppsListView.setBackgroundColor(Color.WHITE);
        allAppsListView.setVerticalScrollBarEnabled(false);
        allAppsListView.setFastScrollEnabled(false);
        allAppsListView.setVerticalFadingEdgeEnabled(false);
        allAppsListView.setFadingEdgeLength(0);
        allAppsListView.setPadding(0, 0, dp(34), 0);
        allAppsListView.setClipToPadding(false);
        allAppsListView.setOnItemClickListener((parent, view, position, id) -> openApp(filteredApps.get(position)));
        allAppsListView.setOnTouchListener((view, event) -> handleAllAppsPullHomeGesture(event));
        allAppsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            AppEntry app = filteredApps.get(position);
            if (allAppsAddMode) {
                return startAllAppsDrag(view, app);
            }
            showAppActions(app);
            return true;
        });

        allAppsListLayer = new FrameLayout(this);
        allAppsListLayer.addView(allAppsListView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        alphabetIndexBar = createAlphabetIndexBar();
        FrameLayout.LayoutParams indexParams = new FrameLayout.LayoutParams(
                dp(32),
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.RIGHT | Gravity.CENTER_VERTICAL
        );
        indexParams.setMargins(0, dp(18), dp(5), dp(18));
        allAppsListLayer.addView(alphabetIndexBar, indexParams);
        allAppsDropTray = createAllAppsDropTray();
        FrameLayout.LayoutParams trayParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(118),
                Gravity.BOTTOM
        );
        trayParams.setMargins(dp(12), 0, dp(12), dp(14));
        allAppsListLayer.addView(allAppsDropTray, trayParams);
        page.addView(allAppsListLayer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        setAllAppsAddMode(false);
        filterApps(searchBox.getText().toString());
        page.post(page::requestApplyInsets);
        return page;
    }

    private LinearLayout createAllAppsDropTray() {
        LinearLayout tray = new LinearLayout(this);
        tray.setOrientation(LinearLayout.HORIZONTAL);
        tray.setGravity(Gravity.CENTER);
        tray.setPadding(dp(10), dp(10), dp(10), dp(10));
        tray.setBackground(new DropTrayDrawable());
        tray.setVisibility(View.GONE);
        tray.setAlpha(0f);
        tray.setTranslationY(dp(24));
        tray.addView(createAllAppsDropTarget(DROP_TARGET_DESKTOP), new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        ));
        View spacer = new View(this);
        tray.addView(spacer, new LinearLayout.LayoutParams(dp(10), 1));
        tray.addView(createAllAppsDropTarget(DROP_TARGET_DOCK), new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        ));
        return tray;
    }

    private View createAllAppsDropTarget(int target) {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.HORIZONTAL);
        box.setGravity(Gravity.CENTER_VERTICAL);
        box.setPadding(dp(14), 0, dp(14), 0);
        box.setBackground(new DropTargetDrawable(false));
        box.setClickable(true);
        box.setFocusable(true);
        box.setOnDragListener((view, event) -> handleAllAppsDropTargetDrag(view, event, target));
        box.setOnClickListener(view -> {
            if (pendingAllAppsDropApp == null) {
                return;
            }
            AppEntry app = pendingAllAppsDropApp;
            pendingAllAppsDropApp = null;
            performAllAppsDrop(app, target);
            showAllAppsDropTray(false);
        });

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(new ActionIconDrawable(target == DROP_TARGET_DESKTOP ? ACTION_HOME : ACTION_DOCK, false));
        box.addView(icon, new LinearLayout.LayoutParams(dp(38), dp(38)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dp(12), 0, 0, 0);
        box.addView(texts, textParams);

        TextView title = new TextView(this);
        title.setText(target == DROP_TARGET_DESKTOP ? R.string.drag_target_desktop : R.string.drag_target_dock);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.rgb(239, 242, 243));
        title.setSingleLine(true);
        texts.addView(title);

        TextView summary = new TextView(this);
        summary.setText(target == DROP_TARGET_DESKTOP ? R.string.drag_target_desktop_summary : R.string.drag_target_dock_summary);
        summary.setTextSize(11);
        summary.setTextColor(Color.rgb(163, 170, 176));
        summary.setSingleLine(true);
        summary.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(summary);
        return box;
    }

    private LinearLayout createAlphabetIndexBar() {
        LinearLayout indexBar = new LinearLayout(this);
        indexBar.setOrientation(LinearLayout.VERTICAL);
        indexBar.setGravity(Gravity.CENTER);
        indexBar.setPadding(0, dp(6), 0, dp(6));
        indexBar.setClipChildren(false);
        indexBar.setClipToPadding(false);
        indexBar.setClickable(true);
        indexBar.setFocusable(true);
        indexBar.setBackground(rounded(Color.argb(226, 255, 255, 255), dp(16), dp(1), Color.rgb(215, 217, 218)));
        alphabetIndexItems.clear();
        String[] letters = getFastIndexLetters();
        for (String letter : letters) {
            TextView item = new TextView(this);
            item.setText(letter);
            item.setTextSize(10);
            item.setTypeface(Typeface.DEFAULT_BOLD);
            item.setTextColor(Color.rgb(92, 98, 104));
            item.setGravity(Gravity.CENTER);
            item.setIncludeFontPadding(false);
            item.setClickable(false);
            item.setFocusable(false);
            alphabetIndexItems.add(item);
            indexBar.addView(item, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0,
                    1f
            ));
        }
        indexBar.setOnTouchListener((view, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                    || event.getActionMasked() == MotionEvent.ACTION_MOVE
                    || event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                handleAlphabetIndexTouch(view, event);
                return true;
            }
            return false;
        });
        return indexBar;
    }

    private View createRecentAppsPanel() {
        recentAppsPanel = new LinearLayout(this);
        recentAppsPanel.setOrientation(LinearLayout.VERTICAL);
        recentAppsPanel.setBackgroundColor(Color.rgb(247, 247, 245));
        recentAppsPanel.setPadding(dp(18), dp(10), dp(18), dp(12));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        recentAppsPanel.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
        ));

        TextView title = new TextView(this);
        title.setText(R.string.recent_apps);
        title.setTextColor(Color.rgb(134, 138, 142));
        title.setTextSize(14);
        title.setSingleLine(true);
        header.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView clear = new TextView(this);
        clear.setText(R.string.clear_recent_apps);
        clear.setTextColor(Color.rgb(138, 92, 86));
        clear.setTextSize(13);
        clear.setGravity(Gravity.CENTER);
        clear.setOnClickListener(v -> clearRecentApps());
        header.addView(clear, new LinearLayout.LayoutParams(dp(64), ViewGroup.LayoutParams.MATCH_PARENT));

        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recentAppsPanel.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(92)
        ));

        recentAppsRow = new LinearLayout(this);
        recentAppsRow.setOrientation(LinearLayout.HORIZONTAL);
        recentAppsRow.setGravity(Gravity.CENTER_VERTICAL);
        scrollView.addView(recentAppsRow, new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        recentAppsPanel.setVisibility(View.GONE);
        return recentAppsPanel;
    }

    private View createRecentAppButton(AppEntry app) {
        LinearLayout button = new LinearLayout(this);
        button.setOrientation(LinearLayout.VERTICAL);
        button.setGravity(Gravity.CENTER);
        button.setPadding(dp(6), dp(7), dp(6), dp(5));
        button.setClickable(true);
        button.setFocusable(true);
        button.setOnClickListener(v -> openApp(app));
        button.setOnLongClickListener(v -> {
            showAppActions(app);
            return true;
        });

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(createDisplayIcon(app));
        button.addView(icon, new LinearLayout.LayoutParams(dp(46), dp(46)));

        TextView label = new TextView(this);
        label.setText(app.label);
        label.setTextColor(Color.rgb(82, 86, 90));
        label.setTextSize(11);
        label.setGravity(Gravity.CENTER);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        labelParams.setMargins(0, dp(6), 0, 0);
        button.addView(label, labelParams);
        return button;
    }

    private View createSearchHistoryPanel() {
        searchHistoryPanel = new LinearLayout(this);
        searchHistoryPanel.setOrientation(LinearLayout.VERTICAL);
        searchHistoryPanel.setBackgroundColor(Color.rgb(247, 247, 245));
        searchHistoryPanel.setPadding(dp(18), dp(2), dp(18), dp(12));

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        searchHistoryPanel.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(30)
        ));

        TextView title = new TextView(this);
        title.setText(R.string.search_history);
        title.setTextColor(Color.rgb(134, 138, 142));
        title.setTextSize(14);
        title.setSingleLine(true);
        header.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView clear = new TextView(this);
        clear.setText(R.string.clear_history);
        clear.setTextColor(Color.rgb(138, 92, 86));
        clear.setTextSize(13);
        clear.setGravity(Gravity.CENTER);
        clear.setOnClickListener(v -> clearSearchHistory());
        header.addView(clear, new LinearLayout.LayoutParams(dp(64), ViewGroup.LayoutParams.MATCH_PARENT));

        HorizontalScrollView scrollView = new HorizontalScrollView(this);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        searchHistoryPanel.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(44)
        ));

        searchHistoryRow = new LinearLayout(this);
        searchHistoryRow.setOrientation(LinearLayout.HORIZONTAL);
        searchHistoryRow.setGravity(Gravity.CENTER_VERTICAL);
        scrollView.addView(searchHistoryRow, new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        searchHistoryPanel.setVisibility(View.GONE);
        return searchHistoryPanel;
    }

    private View createSearchHistoryButton(String query) {
        TextView button = new TextView(this);
        button.setText(query);
        button.setTextColor(Color.rgb(74, 78, 82));
        button.setTextSize(14);
        button.setGravity(Gravity.CENTER);
        button.setSingleLine(true);
        button.setEllipsize(TextUtils.TruncateAt.END);
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setBackground(rounded(Color.WHITE, dp(18), dp(1), Color.rgb(224, 226, 228)));
        button.setOnClickListener(v -> {
            searchBox.setText(query);
            searchBox.setSelection(searchBox.length());
        });
        return button;
    }

    private View createDialPad() {
        LinearLayout pad = new LinearLayout(this);
        pad.setOrientation(LinearLayout.VERTICAL);
        pad.setBackgroundColor(Color.rgb(250, 250, 250));
        String[] labels = {
                "1", "2\nABC", "3\nDEF",
                "4\nGHI", "5\nJKL", "6\nMNO",
                "7\nPQRS", "8\nTUV", "9\nWXYZ",
                "CALL", "0", "DEL"
        };
        for (int rowIndex = 0; rowIndex < 4; rowIndex++) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            pad.addView(row, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(72)
            ));
            for (int column = 0; column < 3; column++) {
                String label = labels[rowIndex * 3 + column];
                TextView key = new TextView(this);
                key.setText("CALL".equals(label) ? getString(R.string.dial_call) : label);
                key.setTextColor(Color.rgb(70, 72, 74));
                key.setTextSize(label.length() > 1 ? 14 : 30);
                key.setTypeface(Typeface.DEFAULT_BOLD);
                key.setGravity(Gravity.CENTER);
                key.setBackground(rounded(Color.rgb(250, 250, 250), 0, dp(1), Color.rgb(232, 233, 234)));
                key.setOnClickListener(v -> handleDialKey(label));
                row.addView(key, new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1f
                ));
            }
        }
        return pad;
    }

    private View createSettingsView() {
        syncLegacySettingsFlags();
        if (settingsPage == SETTINGS_PAGE_THEME) {
            return createThemeSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_MANAGEMENT) {
            return createManagementSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_ANIMATION) {
            return createPageAnimationSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_ICON_STYLE) {
            return createIconStyleSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_ICON_SIZE) {
            return createIconSizeSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_LABEL_SIZE) {
            return createLabelSizeSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_SORT) {
            return createSortSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_HIDDEN_APPS) {
            return createHiddenAppsSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_QUICK_LAUNCH) {
            return createQuickLaunchSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_USAGE) {
            return createUsageRecordsSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_RESET) {
            return createResetDesktopSettingsView();
        }
        if (settingsPage == SETTINGS_PAGE_ABOUT) {
            return createAboutSettingsView();
        }
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackground(new SettingsPageBackgroundDrawable());

        LinearLayout titleBar = new LinearLayout(this);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(4), dp(6), dp(4), 0);
        titleBar.setBackground(new SettingsHeaderDrawable());
        page.addView(titleBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        ));
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets == null ? 0 : insets.getSystemWindowInsetTop();
            int targetHeight = dp(60) + topInset;
            titleBar.setPadding(dp(4), topInset, dp(4), 0);
            ViewGroup.LayoutParams params = titleBar.getLayoutParams();
            if (params != null && params.height != targetHeight) {
                params.height = targetHeight;
                titleBar.setLayoutParams(params);
            }
            return insets;
        });

        ImageView back = new ImageView(this);
        back.setImageDrawable(new BackGlyphDrawable(Color.rgb(74, 80, 86)));
        back.setScaleType(ImageView.ScaleType.CENTER);
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(v -> showDesktop());
        FrameLayout backSlot = new FrameLayout(this);
        FrameLayout.LayoutParams settingsBackParams = new FrameLayout.LayoutParams(dp(52), dp(52), Gravity.CENTER_VERTICAL);
        settingsBackParams.setMargins(dp(20), 0, 0, 0);
        backSlot.addView(back, settingsBackParams);
        titleBar.addView(backSlot, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText(R.string.desktop_settings_short);
        title.setTextColor(Color.rgb(82, 86, 90));
        title.setTextSize(21);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine(true);
        titleBar.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView spacer = new TextView(this);
        titleBar.addView(spacer, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        page.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(28), dp(16), dp(32));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView section = createSettingsSectionTitle(R.string.single_panel_view);
        content.addView(section);
        content.addView(createGridChoiceCard());
        addVerticalSpace(content, dp(24));

        LinearLayout rows = createSettingsCard();
        addSettingsRow(rows, createLargeSettingsRow(R.string.desktop_theme, getString(R.string.desktop_theme_subtitle),
                createSettingsIcon("theme"),
                v -> showDesktopThemeChooser()));
        addSettingsRow(rows, createLargeSettingsRow(R.string.desktop_wallpaper, getString(R.string.desktop_wallpaper_subtitle),
                createSettingsIcon("wallpaper"),
                v -> openWallpaperSettings()));
        addSettingsRow(rows, createLargeSettingsRow(R.string.page_animation, getString(R.string.page_animation_subtitle),
                createSettingsIcon("animation"),
                v -> showPageAnimationChooser()));
        addSettingsRow(rows, createLargeSettingsRow(R.string.app_icons,
                getString(R.string.app_icons_subtitle_line_format, getIconStyleName()),
                createSettingsIcon("icon"),
                v -> showIconStyleChooser()));
        content.addView(rows);
        addVerticalSpace(content, dp(24));

        LinearLayout obsessionCard = createSettingsCard();
        addSettingsRow(obsessionCard, createSimpleSettingsRow(R.string.obsession_options, null, true,
                createSettingsIcon("manage"),
                v -> showObsessionOptions()));
        content.addView(obsessionCard);
        addVerticalSpace(content, dp(18));

        content.addView(createSwitchCard(R.string.transparent_theme, PREF_TRANSPARENT_THEME,
                transparentThemeEnabled, checked -> {
                    transparentThemeEnabled = checked;
                    recycleFrostedWallpaper();
                    applyDesktopTheme();
                }));
        addVerticalSpace(content, dp(18));

        content.addView(createSwitchCard(R.string.fast_launch, PREF_FAST_LAUNCH,
                fastLaunchEnabled, checked -> {
                    fastLaunchEnabled = checked;
                    updateDesktopQuickLaunch();
                    updateDesktopHint();
                }));
        TextView fastLaunchTip = new TextView(this);
        fastLaunchTip.setText(R.string.fast_launch_tip_settings);
        fastLaunchTip.setTextColor(Color.rgb(118, 122, 126));
        fastLaunchTip.setTextSize(14);
        fastLaunchTip.setGravity(Gravity.LEFT);
        LinearLayout.LayoutParams tipParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        tipParams.setMargins(dp(10), dp(12), dp(10), dp(22));
        content.addView(fastLaunchTip, tipParams);

        content.addView(createSettingsSectionTitle(R.string.more_settings));
        LinearLayout moreRows = createSettingsCard();
        addSettingsRow(moreRows, createSimpleSettingsRow(R.string.check_update, getAppVersionLabel(),
                false, createSettingsIcon("version"), v -> checkForUpdates()));
        addSettingsRow(moreRows, createSimpleSettingsRow(R.string.close_battery_optimization, null,
                true, createSettingsIcon("power"), v -> openBatteryOptimizationSettings()));
        addSettingsRow(moreRows, createSimpleSettingsRow(R.string.about_us, null,
                true, createSettingsIcon("about"), v -> showAboutDialog()));
        content.addView(moreRows);
        page.post(page::requestApplyInsets);

        return page;
    }

    private View createSettingsSubPage(int titleRes, int parentPage, View body) {
        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setBackground(new SettingsPageBackgroundDrawable());

        LinearLayout titleBar = new LinearLayout(this);
        titleBar.setOrientation(LinearLayout.HORIZONTAL);
        titleBar.setGravity(Gravity.CENTER_VERTICAL);
        titleBar.setPadding(dp(4), dp(6), dp(4), 0);
        titleBar.setBackground(new SettingsHeaderDrawable());
        page.addView(titleBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        ));
        page.setOnApplyWindowInsetsListener((view, insets) -> {
            int topInset = insets == null ? 0 : insets.getSystemWindowInsetTop();
            int targetHeight = dp(60) + topInset;
            titleBar.setPadding(dp(4), topInset, dp(4), 0);
            ViewGroup.LayoutParams params = titleBar.getLayoutParams();
            if (params != null && params.height != targetHeight) {
                params.height = targetHeight;
                titleBar.setLayoutParams(params);
            }
            return insets;
        });

        ImageView back = new ImageView(this);
        back.setImageDrawable(new BackGlyphDrawable(Color.rgb(74, 80, 86)));
        back.setScaleType(ImageView.ScaleType.CENTER);
        back.setClickable(true);
        back.setFocusable(true);
        back.setOnClickListener(v -> {
            navigateSettingsPage(parentPage, SETTINGS_PAGE_MAIN);
            refreshSettingsView();
        });
        FrameLayout backSlot = new FrameLayout(this);
        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(dp(52), dp(52), Gravity.CENTER_VERTICAL);
        backParams.setMargins(dp(20), 0, 0, 0);
        backSlot.addView(back, backParams);
        titleBar.addView(backSlot, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextColor(Color.rgb(82, 86, 90));
        title.setTextSize(21);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine(true);
        titleBar.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView spacer = new TextView(this);
        titleBar.addView(spacer, new LinearLayout.LayoutParams(dp(84), ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
        page.addView(scrollView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        ));
        scrollView.addView(body, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        page.post(page::requestApplyInsets);
        return page;
    }

    private LinearLayout createSettingsSubPageContent() {
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(28), dp(16), dp(32));
        return content;
    }

    private void navigateSettingsPage(int page, int parentPage) {
        settingsPage = page;
        settingsParentPage = parentPage;
        syncLegacySettingsFlags();
    }

    private void syncLegacySettingsFlags() {
        showingThemeSettings = settingsPage == SETTINGS_PAGE_THEME;
        showingManagementSettings = settingsPage == SETTINGS_PAGE_MANAGEMENT;
    }

    private View createGridChoiceCard() {
        LinearLayout card = createSettingsCard();
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.addView(createGridChoice(12), new LinearLayout.LayoutParams(
                0,
                dp(208),
                1f
        ));
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(226, 227, 224));
        card.addView(divider, new LinearLayout.LayoutParams(dp(1), ViewGroup.LayoutParams.MATCH_PARENT));
        card.addView(createGridChoice(20), new LinearLayout.LayoutParams(
                0,
                dp(208),
                1f
        ));
        return card;
    }

    private View createGridChoice(int mode) {
        LinearLayout choice = new LinearLayout(this);
        choice.setOrientation(LinearLayout.VERTICAL);
        choice.setGravity(Gravity.CENTER);
        choice.setClickable(true);
        choice.setFocusable(true);
        choice.setOnClickListener(v -> {
            desktopMode = mode;
            preferences.edit().putInt(PREF_LAUNCHER_MODE, mode).apply();
            desktopGrid.setNumColumns(getDesktopColumns());
            updateDesktopGridInsets();
            updateDockBackground();
            clampDesktopPage();
            desktopAdapter.notifyDataSetChanged();
            desktopGrid.invalidateViews();
            desktopGrid.requestLayout();
            updateDesktopStatus();
            updatePageIndicator();
            root.removeView(settingsView);
            settingsView = createSettingsView();
            root.addView(settingsView);
        });

        FrameLayout previewWrap = new FrameLayout(this);
        LinearLayout.LayoutParams previewWrapParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(148)
        );
        choice.addView(previewWrap, previewWrapParams);

        ImageView preview = new ImageView(this);
        preview.setScaleType(ImageView.ScaleType.FIT_CENTER);
        preview.setImageDrawable(new SettingsGridPreviewDrawable(mode, desktopMode == mode));
        previewWrap.addView(preview, new FrameLayout.LayoutParams(dp(92), dp(146), Gravity.CENTER));

        if (desktopMode == mode) {
            View check = new View(this);
            check.setBackground(new SettingsCheckDrawable());
            FrameLayout.LayoutParams checkParams = new FrameLayout.LayoutParams(dp(34), dp(34), Gravity.TOP | Gravity.RIGHT);
            checkParams.setMargins(0, dp(12), dp(38), 0);
            previewWrap.addView(check, checkParams);
        }

        TextView label = new TextView(this);
        label.setText(mode == 12 ? R.string.twelve_grid : R.string.twenty_grid);
        label.setTextColor(Color.rgb(62, 66, 70));
        label.setTextSize(19);
        label.setGravity(Gravity.CENTER);
        choice.addView(label, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        return choice;
    }

    private TextView createSettingsSectionTitle(int titleRes) {
        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(20);
        title.setTextColor(Color.rgb(121, 124, 128));
        title.setGravity(Gravity.CENTER_VERTICAL);
        title.setPadding(dp(24), 0, 0, dp(11));
        return title;
    }

    private LinearLayout createSettingsCard() {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setBackground(new SettingsCardDrawable(dp(6)));
        card.setClipToOutline(false);
        card.setElevation(dp(1));
        return card;
    }

    private void addSettingsRow(LinearLayout parent, View row) {
        if (parent.getChildCount() > 0) {
            View divider = new View(this);
            divider.setBackgroundColor(Color.rgb(232, 233, 231));
            LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    dp(1)
            );
            dividerParams.setMargins(dp(26), 0, dp(1), 0);
            parent.addView(divider, dividerParams);
        }
        parent.addView(row);
    }

    private View createSettingsRow(int titleRes, int subtitleRes, String glyph, View.OnClickListener listener) {
        return createSettingsRowText(titleRes, getString(subtitleRes), glyph, listener);
    }

    private View createSettingsRowText(int titleRes, CharSequence subtitleText, String glyph, View.OnClickListener listener) {
        return createLargeSettingsRow(titleRes, subtitleText, createSettingsIcon(glyph), listener);
    }

    private Drawable createSettingsIcon(String kind) {
        int iconRes;
        if ("theme".equals(kind)) {
            iconRes = R.drawable.ic_settings_palette;
        } else if ("wallpaper".equals(kind)) {
            iconRes = R.drawable.ic_settings_wallpaper;
        } else if ("animation".equals(kind)) {
            iconRes = R.drawable.ic_settings_animation;
        } else if ("icon".equals(kind)) {
            iconRes = R.drawable.ic_settings_apps;
        } else if ("manage".equals(kind)) {
            iconRes = R.drawable.ic_settings_tune;
        } else if ("version".equals(kind)) {
            iconRes = R.drawable.ic_settings_article;
        } else if ("power".equals(kind)) {
            iconRes = R.drawable.ic_settings_battery;
        } else if ("about".equals(kind)) {
            iconRes = R.drawable.ic_settings_info;
        } else {
            iconRes = R.drawable.ic_settings_tune;
        }
        Drawable drawable = getDrawable(iconRes);
        return drawable == null ? null : drawable.mutate();
    }

    private View createLargeSettingsRow(int titleRes, CharSequence subtitleText, Drawable iconDrawable,
                                        View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(32), dp(17), dp(24), dp(17));
        row.setMinimumHeight(dp(104));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setPadding(dp(10), dp(10), dp(10), dp(10));
        icon.setImageDrawable(iconDrawable);
        row.addView(icon, new LinearLayout.LayoutParams(dp(58), dp(58)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dp(24), 0, dp(12), 0);
        row.addView(texts, textParams);

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(21);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText(subtitleText);
        subtitle.setTextSize(15);
        subtitle.setTextColor(Color.rgb(128, 132, 136));
        subtitle.setSingleLine(false);
        subtitle.setMaxLines(2);
        subtitle.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(subtitle);

        ImageView arrow = new ImageView(this);
        arrow.setScaleType(ImageView.ScaleType.CENTER);
        arrow.setImageDrawable(new SettingsArrowDrawable());
        row.addView(arrow, new LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.MATCH_PARENT));
        return row;
    }

    private View createSimpleSettingsRow(int titleRes, CharSequence valueText, boolean showArrow,
                                         Drawable iconDrawable, View.OnClickListener listener) {
        return createSimpleSettingsRowText(getText(titleRes), valueText, showArrow, iconDrawable, listener);
    }

    private View createSimpleSettingsRowText(CharSequence titleText, CharSequence valueText, boolean showArrow,
                                             Drawable iconDrawable, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(iconDrawable == null ? dp(26) : dp(32), dp(11), dp(20), dp(11));
        row.setMinimumHeight(iconDrawable == null ? dp(70) : dp(76));
        row.setClickable(listener != null);
        row.setFocusable(listener != null);
        if (listener != null) {
            row.setOnClickListener(listener);
        }

        if (iconDrawable != null) {
            ImageView icon = new ImageView(this);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setPadding(dp(9), dp(9), dp(9), dp(9));
            icon.setImageDrawable(iconDrawable);
            row.addView(icon, new LinearLayout.LayoutParams(dp(50), dp(50)));
        }

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(iconDrawable == null ? 20 : 21);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (iconDrawable != null) {
            titleParams.setMargins(dp(20), 0, dp(10), 0);
        } else {
            titleParams.setMargins(0, 0, dp(10), 0);
        }
        row.addView(title, titleParams);

        if (!TextUtils.isEmpty(valueText)) {
            TextView value = new TextView(this);
            value.setText(valueText);
            value.setTextSize(16);
            value.setTextColor(Color.rgb(91, 115, 148));
            value.setSingleLine(true);
            value.setEllipsize(TextUtils.TruncateAt.END);
            row.addView(value, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        if (showArrow) {
            ImageView arrow = new ImageView(this);
            arrow.setScaleType(ImageView.ScaleType.CENTER);
            arrow.setImageDrawable(new SettingsArrowDrawable());
            row.addView(arrow, new LinearLayout.LayoutParams(dp(28), ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return row;
    }

    private View createChoiceSettingsRow(CharSequence titleText, CharSequence valueText,
                                         boolean selected, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(26), dp(14), dp(20), dp(14));
        row.setMinimumHeight(dp(82));
        row.setClickable(true);
        row.setFocusable(true);
        row.setOnClickListener(listener);

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        row.addView(texts, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(20);
        title.setTextColor(selected ? Color.rgb(185, 76, 68) : Color.rgb(63, 67, 72));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(title);

        if (!TextUtils.isEmpty(valueText)) {
            TextView value = new TextView(this);
            value.setText(valueText);
            value.setTextSize(14);
            value.setTextColor(Color.rgb(128, 132, 136));
            value.setSingleLine(false);
            value.setMaxLines(2);
            value.setEllipsize(TextUtils.TruncateAt.END);
            texts.addView(value);
        }

        FrameLayout checkSlot = new FrameLayout(this);
        if (selected) {
            View check = new View(this);
            check.setBackground(new SettingsCheckDrawable());
            checkSlot.addView(check, new FrameLayout.LayoutParams(dp(30), dp(30), Gravity.CENTER));
        }
        row.addView(checkSlot, new LinearLayout.LayoutParams(dp(46), ViewGroup.LayoutParams.MATCH_PARENT));
        return row;
    }

    private View createAppSettingsRow(AppEntry app, CharSequence valueText, View.OnClickListener listener) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(26), dp(12), dp(20), dp(12));
        row.setMinimumHeight(dp(82));
        row.setClickable(listener != null);
        row.setFocusable(listener != null);
        if (listener != null) {
            row.setOnClickListener(listener);
        }

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(createDisplayIcon(app));
        row.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));

        TextView title = new TextView(this);
        title.setText(app == null ? "" : app.label);
        title.setTextSize(20);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.setMargins(dp(18), 0, dp(10), 0);
        row.addView(title, titleParams);

        if (!TextUtils.isEmpty(valueText)) {
            TextView value = new TextView(this);
            value.setText(valueText);
            value.setTextSize(15);
            value.setTextColor(Color.rgb(91, 115, 148));
            value.setSingleLine(true);
            value.setEllipsize(TextUtils.TruncateAt.END);
            row.addView(value);
        }
        return row;
    }

    private View createInfoSettingsRow(CharSequence titleText, CharSequence bodyText, Drawable iconDrawable) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(iconDrawable == null ? dp(26) : dp(32), dp(18), dp(24), dp(18));
        row.setMinimumHeight(dp(104));

        if (iconDrawable != null) {
            ImageView icon = new ImageView(this);
            icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
            icon.setPadding(dp(9), dp(9), dp(9), dp(9));
            icon.setImageDrawable(iconDrawable);
            row.addView(icon, new LinearLayout.LayoutParams(dp(50), dp(50)));
        }

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        if (iconDrawable != null) {
            textParams.setMargins(dp(20), 0, 0, 0);
        }
        row.addView(texts, textParams);

        TextView title = new TextView(this);
        title.setText(titleText);
        title.setTextSize(21);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(false);
        texts.addView(title);

        if (!TextUtils.isEmpty(bodyText)) {
            TextView body = new TextView(this);
            body.setText(bodyText);
            body.setTextSize(15);
            body.setTextColor(Color.rgb(128, 132, 136));
            body.setSingleLine(false);
            body.setLineSpacing(dp(2), 1f);
            LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            bodyParams.setMargins(0, dp(6), 0, 0);
            texts.addView(body, bodyParams);
        }
        return row;
    }

    private View createSwitchRow(int titleRes, String prefKey, boolean checked, SwitchCallback callback) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(14), dp(8), dp(14), dp(8));
        row.setMinimumHeight(dp(56));
        row.setClickable(true);
        row.setFocusable(true);

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(15);
        title.setTextColor(Color.rgb(43, 47, 51));
        title.setSingleLine(false);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Switch toggle = new Switch(this);
        toggle.setChecked(checked);
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean(prefKey, isChecked).apply();
            callback.onChanged(isChecked);
        });
        row.setOnClickListener(v -> toggle.setChecked(!toggle.isChecked()));
        row.addView(toggle);
        return row;
    }

    private View createSwitchCard(int titleRes, String prefKey, boolean checked, SwitchCallback callback) {
        LinearLayout card = createSettingsCard();
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(26), dp(13), dp(18), dp(13));
        row.setMinimumHeight(dp(76));
        row.setClickable(true);
        row.setFocusable(true);
        card.addView(row);

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(20);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(false);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        View toggle = new View(this);
        final boolean[] state = {checked};
        toggle.setBackground(new SettingsSwitchDrawable(state[0]));
        row.addView(toggle, new LinearLayout.LayoutParams(dp(88), dp(48)));
        View.OnClickListener toggleClick = v -> {
            state[0] = !state[0];
            preferences.edit().putBoolean(prefKey, state[0]).apply();
            toggle.setBackground(new SettingsSwitchDrawable(state[0]));
            callback.onChanged(state[0]);
        };
        row.setOnClickListener(toggleClick);
        toggle.setOnClickListener(toggleClick);
        return card;
    }

    private View createManagementSwitchRow(int titleRes, boolean checked, SwitchCallback callback) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(32), dp(13), dp(20), dp(13));
        row.setMinimumHeight(dp(76));
        row.setClickable(true);
        row.setFocusable(true);

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(21);
        title.setTextColor(Color.rgb(63, 67, 72));
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        row.addView(title, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        View toggle = new View(this);
        final boolean[] state = {checked};
        toggle.setBackground(new SettingsSwitchDrawable(state[0]));
        LinearLayout.LayoutParams toggleParams = new LinearLayout.LayoutParams(dp(88), dp(48));
        toggleParams.setMargins(dp(16), 0, 0, 0);
        row.addView(toggle, toggleParams);

        View.OnClickListener clickListener = v -> {
            state[0] = !state[0];
            toggle.setBackground(new SettingsSwitchDrawable(state[0]));
            callback.onChanged(state[0]);
        };
        row.setOnClickListener(clickListener);
        toggle.setOnClickListener(clickListener);
        return row;
    }

    private void showObsessionOptions() {
        navigateSettingsPage(SETTINGS_PAGE_MANAGEMENT, SETTINGS_PAGE_MAIN);
        refreshSettingsView();
    }

    private String getSettingStateName(boolean enabled) {
        return getString(enabled ? R.string.setting_enabled : R.string.setting_disabled);
    }

    private void openWallpaperSettings() {
        Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException firstFailure) {
            try {
                startActivity(new Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException | SecurityException secondFailure) {
                Toast.makeText(this, R.string.open_wallpaper_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getAppVersionLabel() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            if (!TextUtils.isEmpty(packageInfo.versionName)) {
                return "v" + packageInfo.versionName;
            }
        } catch (PackageManager.NameNotFoundException | SecurityException ignored) {
            // Fall through to the local build label.
        }
        return getString(R.string.version_local_build);
    }

    private void checkForUpdates() {
        Toast.makeText(this, getString(R.string.version_info_toast, getAppVersionLabel()), Toast.LENGTH_SHORT).show();
    }

    private void openBatteryOptimizationSettings() {
        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException exception) {
            openSystemSettings();
        }
    }

    private View createPageAnimationSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addPageAnimationChoice(card, PAGE_ANIM_DEFAULT, R.string.page_anim_default);
        addPageAnimationChoice(card, PAGE_ANIM_GRID_FLIP, R.string.page_anim_grid_flip);
        addPageAnimationChoice(card, PAGE_ANIM_SHUTTER, R.string.page_anim_shutter);
        addPageAnimationChoice(card, PAGE_ANIM_CUT_CARD, R.string.page_anim_cut_card);
        content.addView(card);
        return createSettingsSubPage(R.string.page_animation, SETTINGS_PAGE_MAIN, content);
    }

    private void addPageAnimationChoice(LinearLayout card, int value, int labelRes) {
        addSettingsRow(card, createChoiceSettingsRow(getText(labelRes), null, pageAnimation == value, v -> {
            pageAnimation = value;
            preferences.edit().putInt(PREF_PAGE_ANIMATION, pageAnimation).apply();
            Toast.makeText(this, labelRes, Toast.LENGTH_SHORT).show();
            refreshSettingsView();
            if (desktopView.getVisibility() == View.VISIBLE) {
                animateDesktopPageChange(1);
            }
        }));
    }

    private View createIconStyleSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addIconStyleChoice(card, ICON_STYLE_PLATE, R.string.icon_style_plate);
        addIconStyleChoice(card, ICON_STYLE_ORIGINAL, R.string.icon_style_original);
        content.addView(card);
        return createSettingsSubPage(R.string.app_icons, SETTINGS_PAGE_MAIN, content);
    }

    private void addIconStyleChoice(LinearLayout card, int value, int labelRes) {
        addSettingsRow(card, createChoiceSettingsRow(getText(labelRes), null, iconStyle == value, v -> {
            iconStyle = value;
            preferences.edit().putInt(PREF_ICON_STYLE, iconStyle).apply();
            refreshIconAppearance();
            refreshSettingsView();
        }));
    }

    private View createIconSizeSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addIconSizeChoice(card, ICON_SIZE_SMALL, R.string.icon_size_small);
        addIconSizeChoice(card, ICON_SIZE_STANDARD, R.string.icon_size_standard);
        addIconSizeChoice(card, ICON_SIZE_LARGE, R.string.icon_size_large);
        content.addView(card);
        return createSettingsSubPage(R.string.icon_size, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private void addIconSizeChoice(LinearLayout card, int value, int labelRes) {
        addSettingsRow(card, createChoiceSettingsRow(getText(labelRes), null, iconSize == value, v -> {
            iconSize = value;
            preferences.edit().putInt(PREF_ICON_SIZE, iconSize).apply();
            refreshIconAppearance();
            refreshSettingsView();
        }));
    }

    private View createLabelSizeSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addLabelSizeChoice(card, LABEL_SIZE_SMALL, R.string.label_size_small);
        addLabelSizeChoice(card, LABEL_SIZE_STANDARD, R.string.label_size_standard);
        addLabelSizeChoice(card, LABEL_SIZE_LARGE, R.string.label_size_large);
        content.addView(card);
        return createSettingsSubPage(R.string.label_size, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private void addLabelSizeChoice(LinearLayout card, int value, int labelRes) {
        addSettingsRow(card, createChoiceSettingsRow(getText(labelRes), null, labelSize == value, v -> {
            labelSize = value;
            preferences.edit().putInt(PREF_LABEL_SIZE, labelSize).apply();
            refreshLabelAppearance();
            refreshSettingsView();
        }));
    }

    private View createSortSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addSortChoice(card, SORT_CUSTOM, R.string.sort_custom);
        addSortChoice(card, SORT_NAME, R.string.sort_name);
        addSortChoice(card, SORT_INSTALL_TIME, R.string.sort_install_time);
        content.addView(card);
        return createSettingsSubPage(R.string.app_sort, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private void addSortChoice(LinearLayout card, int value, int labelRes) {
        addSettingsRow(card, createChoiceSettingsRow(getText(labelRes), null, sortMode == value, v -> {
            sortMode = value;
            preferences.edit().putInt(PREF_SORT_MODE, sortMode).apply();
            applySortModeToDesktop();
            saveDesktopOrder();
            Toast.makeText(this, labelRes, Toast.LENGTH_SHORT).show();
            refreshSettingsView();
        }));
    }

    private View createHiddenAppsSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        List<AppEntry> hiddenApps = getHiddenApps();
        if (hiddenApps.isEmpty()) {
            LinearLayout empty = createSettingsCard();
            addSettingsRow(empty, createSimpleSettingsRow(R.string.hidden_apps_empty, null,
                    false, createSettingsIcon("manage"), null));
            content.addView(empty);
        } else {
            LinearLayout apps = createSettingsCard();
            for (AppEntry app : hiddenApps) {
                addSettingsRow(apps, createAppSettingsRow(app, getText(R.string.restore_hidden_app_action),
                        v -> {
                            restoreHiddenApp(app);
                            refreshSettingsView();
                        }));
            }
            content.addView(apps);
            addVerticalSpace(content, dp(18));
            LinearLayout actions = createSettingsCard();
            addSettingsRow(actions, createSimpleSettingsRow(R.string.restore_all_hidden_apps, null,
                    false, createSettingsIcon("manage"), v -> {
                        restoreHiddenApps();
                        refreshSettingsView();
                    }));
            content.addView(actions);
        }
        return createSettingsSubPage(R.string.hidden_apps, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private View createQuickLaunchSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        List<AppEntry> pinnedApps = getPinnedQuickLaunchApps();
        if (pinnedApps.isEmpty()) {
            LinearLayout empty = createSettingsCard();
            addSettingsRow(empty, createSimpleSettingsRow(R.string.quick_launch_empty, null,
                    false, createSettingsIcon("icon"), null));
            content.addView(empty);
        } else {
            LinearLayout apps = createSettingsCard();
            for (AppEntry app : pinnedApps) {
                addSettingsRow(apps, createAppSettingsRow(app, getText(R.string.edit_action_unpin_quick_launch),
                        v -> {
                            setQuickLaunchPinned(app, false);
                            refreshSettingsView();
                        }));
            }
            content.addView(apps);
            addVerticalSpace(content, dp(18));
            LinearLayout actions = createSettingsCard();
            addSettingsRow(actions, createSimpleSettingsRow(R.string.clear_pinned_quick_launch, null,
                    false, createSettingsIcon("manage"), v -> {
                        clearPinnedQuickLaunch();
                        refreshSettingsView();
                    }));
            content.addView(actions);
        }
        return createSettingsSubPage(R.string.quick_launch_manage, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private View createUsageRecordsSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addSettingsRow(card, createSimpleSettingsRow(R.string.recent_apps, null,
                false, createSettingsIcon("version"), v -> {
                    clearRecentApps();
                    Toast.makeText(this, R.string.usage_records_cleared_toast, Toast.LENGTH_SHORT).show();
                    refreshSettingsView();
                }));
        addSettingsRow(card, createSimpleSettingsRow(R.string.search_history, null,
                false, createSettingsIcon("version"), v -> {
                    clearSearchHistory();
                    Toast.makeText(this, R.string.usage_records_cleared_toast, Toast.LENGTH_SHORT).show();
                    refreshSettingsView();
                }));
        addSettingsRow(card, createSimpleSettingsRow(R.string.clear_all_usage_records, null,
                false, createSettingsIcon("manage"), v -> {
                    clearUsageRecords();
                    refreshSettingsView();
                }));
        content.addView(card);
        return createSettingsSubPage(R.string.clear_usage_records, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private View createResetDesktopSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addSettingsRow(card, createInfoSettingsRow(getText(R.string.reset_desktop_layout),
                getText(R.string.reset_desktop_layout_message), createSettingsIcon("manage")));
        content.addView(card);
        addVerticalSpace(content, dp(18));
        LinearLayout action = createSettingsCard();
        addSettingsRow(action, createSimpleSettingsRow(R.string.reset_desktop_layout_positive, null,
                false, createSettingsIcon("manage"), v -> resetDesktopLayout()));
        content.addView(action);
        return createSettingsSubPage(R.string.reset_desktop_layout, SETTINGS_PAGE_MANAGEMENT, content);
    }

    private View createAboutSettingsView() {
        LinearLayout content = createSettingsSubPageContent();
        LinearLayout card = createSettingsCard();
        addSettingsRow(card, createInfoSettingsRow(getString(R.string.about_us),
                getString(R.string.about_us_message) + "\n\n" + getString(R.string.app_name) + " " + getAppVersionLabel(),
                createSettingsIcon("about")));
        content.addView(card);
        return createSettingsSubPage(R.string.about_us, SETTINGS_PAGE_MAIN, content);
    }

    private void showAboutDialog() {
        navigateSettingsPage(SETTINGS_PAGE_ABOUT, SETTINGS_PAGE_MAIN);
        refreshSettingsView();
    }

    private void showPageAnimationChooser() {
        navigateSettingsPage(SETTINGS_PAGE_ANIMATION, SETTINGS_PAGE_MAIN);
        refreshSettingsView();
    }

    private String getPageAnimationName() {
        switch (pageAnimation) {
            case PAGE_ANIM_GRID_FLIP:
                return getString(R.string.page_anim_grid_flip);
            case PAGE_ANIM_SHUTTER:
                return getString(R.string.page_anim_shutter);
            case PAGE_ANIM_CUT_CARD:
                return getString(R.string.page_anim_cut_card);
            case PAGE_ANIM_DEFAULT:
            default:
                return getString(R.string.page_anim_default);
        }
    }

    private int normalizePageAnimation(int value) {
        if (value == PAGE_ANIM_GRID_FLIP
                || value == PAGE_ANIM_SHUTTER
                || value == PAGE_ANIM_CUT_CARD) {
            return value;
        }
        return PAGE_ANIM_DEFAULT;
    }

    private void showSortModeChooser() {
        navigateSettingsPage(SETTINGS_PAGE_SORT, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private String getSortModeName() {
        switch (sortMode) {
            case SORT_NAME:
                return getString(R.string.sort_name);
            case SORT_INSTALL_TIME:
                return getString(R.string.sort_install_time);
            case SORT_CUSTOM:
            default:
                return getString(R.string.sort_custom);
        }
    }

    private int normalizeSortMode(int value) {
        if (value == SORT_NAME || value == SORT_INSTALL_TIME) {
            return value;
        }
        return SORT_CUSTOM;
    }

    private void showUsageRecordsCleaner() {
        navigateSettingsPage(SETTINGS_PAGE_USAGE, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private void confirmResetDesktopLayout() {
        navigateSettingsPage(SETTINGS_PAGE_RESET, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private void resetDesktopLayout() {
        sortMode = SORT_NAME;
        currentDesktopPage = 0;
        selectedEditPosition = -1;
        preferences.edit()
                .remove(PREF_APP_ORDER)
                .putInt(PREF_SORT_MODE, sortMode)
                .apply();
        loadHomeApps();
        refreshSettingsIfVisible();
        Toast.makeText(this, R.string.reset_desktop_layout_toast, Toast.LENGTH_SHORT).show();
    }

    private void refreshAppList() {
        exitEditMode();
        loadApps(R.string.refresh_apps_toast);
    }

    private void showDesktopThemeChooser() {
        navigateSettingsPage(SETTINGS_PAGE_THEME, SETTINGS_PAGE_MAIN);
        refreshSettingsView();
    }

    private String getDesktopThemeName() {
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return getString(smartisanTheme.labelResId);
        }
        PantoneTheme pantoneTheme = getPantoneTheme(desktopTheme);
        if (pantoneTheme != null) {
            return pantoneTheme.getLabel();
        }
        switch (desktopTheme) {
            case THEME_GRAPHITE:
                return getString(R.string.theme_graphite);
            case THEME_COPPER:
                return getString(R.string.theme_copper);
            case THEME_ORIGINAL_BLUE:
                return getString(R.string.theme_original_blue);
            case THEME_CLASSIC:
            default:
                return getString(R.string.theme_classic);
        }
    }

    private int normalizeDesktopTheme(int value) {
        if (value == THEME_CLASSIC_BLUE_TEXTURE) {
            return THEME_SMARTISAN_START + 1;
        }
        if (isPantoneThemeValue(value)
                || value == THEME_GRAPHITE || value == THEME_COPPER || value == THEME_ORIGINAL_BLUE
                || getSmartisanTheme(value) != null) {
            return value;
        }
        return THEME_CLASSIC;
    }

    private static boolean isPantoneThemeValue(int value) {
        return getPantoneTheme(value) != null;
    }

    private static PantoneTheme getPantoneTheme(int value) {
        int index = value - THEME_PANTONE_START;
        if (index < 0 || index >= PANTONE_THEMES.length) {
            return null;
        }
        return PANTONE_THEMES[index];
    }

    private static ThemePalette getPantonePalette(int value) {
        PantoneTheme theme = getPantoneTheme(value);
        if (theme == null) {
            return null;
        }
        int base = theme.primaryColor;
        int accent = theme.accentColor;
        boolean light = theme.light;
        int top = mixColor(base, Color.WHITE, light ? 0.10f : 0.04f);
        int bottom = mixColor(base, Color.BLACK, light ? 0.28f : 0.50f);
        int cell = mixColor(accent, Color.WHITE, light ? 0.20f : 0.16f);
        int dock = mixColor(bottom, Color.BLACK, light ? 0.08f : 0.15f);
        return new ThemePalette(top, bottom, cell, dock, accent, light);
    }

    private SmartisanTheme getSmartisanTheme(int value) {
        int index = value - THEME_SMARTISAN_START;
        if (index < 0 || index >= SMARTISAN_THEMES.length) {
            return null;
        }
        return SMARTISAN_THEMES[index];
    }

    private boolean isSmartisanTextureTheme() {
        return getSmartisanTheme(desktopTheme) != null;
    }

    private void showIconStyleChooser() {
        navigateSettingsPage(SETTINGS_PAGE_ICON_STYLE, SETTINGS_PAGE_MAIN);
        refreshSettingsView();
    }

    private String getIconStyleName() {
        switch (iconStyle) {
            case ICON_STYLE_ORIGINAL:
                return getString(R.string.icon_style_original);
            case ICON_STYLE_PLATE:
            default:
                return getString(R.string.icon_style_plate);
        }
    }

    private int normalizeIconStyle(int value) {
        if (value == ICON_STYLE_ORIGINAL) {
            return ICON_STYLE_ORIGINAL;
        }
        return ICON_STYLE_PLATE;
    }

    private void showIconSizeChooser() {
        navigateSettingsPage(SETTINGS_PAGE_ICON_SIZE, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private String getIconSizeName() {
        switch (iconSize) {
            case ICON_SIZE_SMALL:
                return getString(R.string.icon_size_small);
            case ICON_SIZE_LARGE:
                return getString(R.string.icon_size_large);
            case ICON_SIZE_STANDARD:
            default:
                return getString(R.string.icon_size_standard);
        }
    }

    private int normalizeIconSize(int value) {
        if (value == ICON_SIZE_SMALL || value == ICON_SIZE_LARGE) {
            return value;
        }
        return ICON_SIZE_STANDARD;
    }

    private int getDesktopIconSize() {
        int cellWidth = Math.max(1, getDesktopAvailableWidth() / getDesktopColumns());
        int basePx = Math.round(cellWidth * (desktopMode == 12 ? 0.40f : 0.44f));
        int minPx = dp(desktopMode == 12 ? 52 : 40);
        int maxPx = dp(desktopMode == 12 ? 70 : 56);
        int baseDp = Math.round(Math.max(minPx, Math.min(maxPx, basePx))
                / getResources().getDisplayMetrics().density);
        if (iconSize == ICON_SIZE_SMALL) {
            baseDp -= desktopMode == 12 ? 6 : 5;
        } else if (iconSize == ICON_SIZE_LARGE) {
            baseDp += desktopMode == 12 ? 6 : 5;
        }
        return dp(baseDp);
    }

    private int getDesktopAvailableWidth() {
        if (desktopGrid != null && desktopGrid.getWidth() > 0) {
            return desktopGrid.getWidth() - desktopGrid.getPaddingLeft() - desktopGrid.getPaddingRight();
        }
        if (desktopGridLayer != null && desktopGridLayer.getWidth() > 0) {
            return desktopGridLayer.getWidth() - desktopGridLayer.getPaddingLeft() - desktopGridLayer.getPaddingRight();
        }
        if (desktopPage != null && desktopPage.getWidth() > 0) {
            return desktopPage.getWidth() - desktopPage.getPaddingLeft() - desktopPage.getPaddingRight();
        }
        return getResources().getDisplayMetrics().widthPixels;
    }

    private int getSearchIconSize() {
        int baseDp = 48;
        if (iconSize == ICON_SIZE_SMALL) {
            baseDp = 42;
        } else if (iconSize == ICON_SIZE_LARGE) {
            baseDp = 54;
        }
        return dp(baseDp);
    }

    private int getQuickLaunchIconSize() {
        int baseDp = 46;
        if (iconSize == ICON_SIZE_SMALL) {
            baseDp = 42;
        } else if (iconSize == ICON_SIZE_LARGE) {
            baseDp = 50;
        }
        return dp(baseDp);
    }

    private void showLabelSizeChooser() {
        navigateSettingsPage(SETTINGS_PAGE_LABEL_SIZE, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private String getLabelSizeName() {
        switch (labelSize) {
            case LABEL_SIZE_SMALL:
                return getString(R.string.label_size_small);
            case LABEL_SIZE_LARGE:
                return getString(R.string.label_size_large);
            case LABEL_SIZE_STANDARD:
            default:
                return getString(R.string.label_size_standard);
        }
    }

    private int normalizeLabelSize(int value) {
        if (value == LABEL_SIZE_SMALL || value == LABEL_SIZE_LARGE) {
            return value;
        }
        return LABEL_SIZE_STANDARD;
    }

    private float getDesktopLabelTextSize() {
        float baseSp = desktopMode == 12 ? 12f : 11f;
        if (labelSize == LABEL_SIZE_SMALL) {
            return baseSp - 1f;
        }
        if (labelSize == LABEL_SIZE_LARGE) {
            return baseSp + 1f;
        }
        return baseSp;
    }

    private float getQuickLaunchLabelTextSize() {
        if (labelSize == LABEL_SIZE_SMALL) {
            return 9f;
        }
        if (labelSize == LABEL_SIZE_LARGE) {
            return 11f;
        }
        return 10f;
    }

    private void refreshIconAppearance() {
        if (desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
        if (searchBox != null) {
            updateRecentAppsPanel(TextUtils.isEmpty(searchBox.getText().toString().trim()));
        }
        updateDesktopQuickLaunch();
    }

    private void refreshLabelAppearance() {
        if (desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        updateDesktopQuickLaunch();
    }

    private void refreshSettingsView() {
        if (settingsView == null) {
            return;
        }
        boolean wasVisible = settingsView.getVisibility() == View.VISIBLE;
        root.removeView(settingsView);
        settingsView = createSettingsView();
        root.addView(settingsView);
        settingsView.setVisibility(wasVisible ? View.VISIBLE : View.GONE);
    }

    private void handleDesktopGesture(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                downX = event.getRawX();
                downTime = event.getEventTime();
                initialPointerDistance = 0f;
                pinchHandled = false;
                recycleVelocityTracker();
                velocityTracker = VelocityTracker.obtain();
                velocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    initialPointerDistance = pointerDistance(event);
                    pinchHandled = false;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (velocityTracker != null) {
                    velocityTracker.addMovement(event);
                }
                if (event.getPointerCount() >= 2 && initialPointerDistance > 0f && !pinchHandled) {
                    float currentDistance = pointerDistance(event);
                    if (initialPointerDistance - currentDistance > Math.max(dp(64), 200)) {
                        enterEditMode();
                        pinchHandled = true;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (pinchHandled) {
                    recycleVelocityTracker();
                    break;
                }
                float deltaY = downY - event.getRawY();
                float deltaX = downX - event.getRawX();
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                long duration = event.getEventTime() - downTime;
                int height = getResources().getDisplayMetrics().heightPixels;
                int width = getResources().getDisplayMetrics().widthPixels;
                float velocityX = getTrackedXVelocity(event);
                float velocityY = getTrackedYVelocity(event);
                boolean fromDesktop = downY > dp(64) && downY < height - dp(72);
                float minSearchDistance = Math.max(dp(56), height * 0.055f);
                boolean verticalFling = -velocityY > dp(PAGE_FLING_VELOCITY_DP)
                        && deltaY > dp(34)
                        && absDeltaX < Math.max(deltaY * 0.72f, dp(56));
                boolean deliberateSearch = duration >= 70 && duration <= 1200;
                if (!editMode
                        && fromDesktop
                        && (deltaY > minSearchDistance || verticalFling)
                        && absDeltaX < deltaY * 0.58f
                        && (deliberateSearch || verticalFling)) {
                    showSearch();
                } else {
                    float minPageDistance = Math.max(dp(74), width * 0.10f);
                    boolean horizontalFling = Math.abs(velocityX) > dp(PAGE_FLING_VELOCITY_DP)
                            && absDeltaX > dp(30)
                            && absDeltaX > absDeltaY * 0.82f
                            && duration < 650;
                    boolean horizontalSwipe = absDeltaX > minPageDistance
                            && absDeltaX > absDeltaY * 1.28f
                            && duration < 900;
                    if (!pageAnimationRunning && (horizontalSwipe || horizontalFling)) {
                        int direction = horizontalFling ? (velocityX < 0 ? 1 : -1) : (deltaX > 0 ? 1 : -1);
                        if (editMode && selectedEditPosition >= 0) {
                            moveSelectedAppToAdjacentPage(direction);
                        } else if (direction > 0) {
                            if (canShowAdjacentDesktopPage(1)) {
                                showNextDesktopPage();
                            } else {
                                animateDesktopEdgeResistance(1);
                            }
                        } else {
                            if (canShowAdjacentDesktopPage(-1)) {
                                showPreviousDesktopPage();
                            } else {
                                animateDesktopEdgeResistance(-1);
                            }
                        }
                    }
                }
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
            default:
                break;
        }
    }

    private boolean handleDockGesture(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getRawY();
                downX = event.getRawX();
                downTime = event.getEventTime();
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!editMode) {
                    applyDockGestureFeedback(Math.max(0f, downY - event.getRawY()));
                }
                return false;
            case MotionEvent.ACTION_UP:
                float deltaY = downY - event.getRawY();
                float deltaX = downX - event.getRawX();
                float absDeltaX = Math.abs(deltaX);
                long duration = event.getEventTime() - downTime;
                int height = getResources().getDisplayMetrics().heightPixels;
                float minSettingsDistance = Math.max(dp(48), height * 0.045f);
                if (!editMode
                        && deltaY > minSettingsDistance
                        && absDeltaX < deltaY * 0.65f
                        && duration >= 55
                        && duration <= 1200) {
                    resetDockGestureFeedback();
                    showSettings();
                    return true;
                }
                resetDockGestureFeedback();
                return false;
            case MotionEvent.ACTION_CANCEL:
                resetDockGestureFeedback();
                return false;
            default:
                return false;
        }
    }

    private float getTrackedXVelocity(MotionEvent event) {
        if (velocityTracker == null) {
            return 0f;
        }
        velocityTracker.addMovement(event);
        velocityTracker.computeCurrentVelocity(1000);
        return velocityTracker.getXVelocity();
    }

    private float getTrackedYVelocity(MotionEvent event) {
        if (velocityTracker == null) {
            return 0f;
        }
        velocityTracker.addMovement(event);
        velocityTracker.computeCurrentVelocity(1000);
        return velocityTracker.getYVelocity();
    }

    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private boolean canShowAdjacentDesktopPage(int direction) {
        int pageCount = getDesktopPageCount();
        if (pageCount <= 1) {
            return false;
        }
        if (loopDesktopPages) {
            return true;
        }
        return direction > 0 ? currentDesktopPage < pageCount - 1 : currentDesktopPage > 0;
    }

    private void animateDesktopEdgeResistance(int direction) {
        if (desktopGrid == null) {
            return;
        }
        performDesktopHaptic(HapticFeedbackConstants.CLOCK_TICK);
        int offset = direction * -dp(18);
        desktopGrid.animate().cancel();
        desktopGrid.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        desktopGrid.animate()
                .translationX(offset)
                .scaleX(0.992f)
                .setDuration(90L)
                .setInterpolator(originalMotionInterpolator())
                .withEndAction(() -> desktopGrid.animate()
                        .translationX(0f)
                        .scaleX(1f)
                        .setDuration(170L)
                        .setInterpolator(originalMotionInterpolator())
                        .withEndAction(() -> desktopGrid.setLayerType(View.LAYER_TYPE_NONE, null))
                        .start())
                .start();
    }

    private void applyDockGestureFeedback(float upwardDistance) {
        if (desktopDock == null) {
            return;
        }
        float progress = Math.min(1f, upwardDistance / Math.max(1f, dp(92)));
        desktopDock.setTranslationY(-dp(8) * progress);
        desktopDock.setScaleX(1f - 0.012f * progress);
        desktopDock.setScaleY(1f - 0.018f * progress);
        desktopDock.setAlpha(1f - 0.10f * progress);
    }

    private void resetDockGestureFeedback() {
        if (desktopDock == null) {
            return;
        }
        desktopDock.animate().cancel();
        desktopDock.animate()
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .alpha(1f)
                .setDuration(150L)
                .setInterpolator(originalMotionInterpolator())
                .start();
    }

    private float pointerDistance(MotionEvent event) {
        if (event.getPointerCount() < 2) {
            return 0f;
        }
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private void performDesktopHaptic(int feedbackConstant) {
        if (!hapticFeedbackEnabled) {
            return;
        }
        if (desktopGrid != null) {
            desktopGrid.performHapticFeedback(feedbackConstant);
        } else if (desktopView != null) {
            desktopView.performHapticFeedback(feedbackConstant);
        }
    }

    private void showNextDesktopPage() {
        int pageCount = getDesktopPageCount();
        if (currentDesktopPage < pageCount - 1) {
            showDesktopPage(currentDesktopPage + 1, 1);
        } else if (loopDesktopPages && pageCount > 1) {
            showDesktopPage(0, 1);
        }
    }

    private void showPreviousDesktopPage() {
        if (currentDesktopPage > 0) {
            showDesktopPage(currentDesktopPage - 1, -1);
        } else if (loopDesktopPages && getDesktopPageCount() > 1) {
            showDesktopPage(getDesktopPageCount() - 1, -1);
        }
    }

    private void showDesktopPage(int targetPage) {
        int pageCount = getDesktopPageCount();
        if (targetPage < 0 || targetPage >= pageCount || targetPage == currentDesktopPage) {
            return;
        }
        int direction = targetPage > currentDesktopPage ? 1 : -1;
        showDesktopPage(targetPage, direction);
    }

    private void showDesktopPage(int targetPage, int direction) {
        int pageCount = getDesktopPageCount();
        if (targetPage < 0 || targetPage >= pageCount || targetPage == currentDesktopPage) {
            return;
        }
        Bitmap outgoingBitmap = shouldCaptureOutgoingPage() ? captureDesktopGridBitmap() : null;
        currentDesktopPage = targetPage;
        selectedEditPosition = -1;
        desktopAdapter.notifyDataSetChanged();
        desktopGrid.invalidateViews();
        clearDesktopGridDrawingCaches();
        performDesktopHaptic(HapticFeedbackConstants.VIRTUAL_KEY);
        animateDesktopPageChange(direction, outgoingBitmap);
        updateDesktopStatus();
        updatePageIndicator();
    }

    private void handlePageIndicatorTap(int targetPage) {
        if (editMode && selectedEditPosition >= 0) {
            moveSelectedAppToPage(targetPage);
            return;
        }
        showDesktopPage(targetPage);
    }

    private void moveSelectedAppToPage(int targetPage) {
        int pageCount = getDesktopPageCount();
        int selectedAbsolutePosition = currentDesktopPage * getDesktopPageSize() + selectedEditPosition;
        if (targetPage < 0
                || targetPage >= pageCount
                || selectedAbsolutePosition < 0
                || selectedAbsolutePosition >= desktopApps.size()) {
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            updatePageIndicator();
            return;
        }
        moveAppToPage(selectedAbsolutePosition, targetPage);
    }

    private void moveAppToPage(int sourceAbsolutePosition, int targetPage) {
        int animationDirection = targetPage >= currentDesktopPage ? 1 : -1;
        moveAppToPage(sourceAbsolutePosition, targetPage, animationDirection);
    }

    private void moveAppToPage(int sourceAbsolutePosition, int targetPage, int animationDirection) {
        int pageCount = getDesktopPageCount();
        if (targetPage < 0
                || targetPage >= pageCount
                || sourceAbsolutePosition < 0
                || sourceAbsolutePosition >= desktopApps.size()) {
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            updatePageIndicator();
            return;
        }
        int targetIndex = Math.min((targetPage + 1) * getDesktopPageSize(), desktopApps.size());
        if (sourceAbsolutePosition < targetIndex) {
            targetIndex--;
        }
        Bitmap outgoingBitmap = shouldCaptureOutgoingPage() ? captureDesktopGridBitmap() : null;
        moveDesktopApp(sourceAbsolutePosition, targetIndex);
        currentDesktopPage = targetPage;
        selectedEditPosition = -1;
        desktopAdapter.notifyDataSetChanged();
        performDesktopHaptic(HapticFeedbackConstants.VIRTUAL_KEY);
        animateDesktopPageChange(animationDirection, outgoingBitmap);
        updateDesktopStatus();
        updatePageIndicator();
    }

    private void moveSelectedAppToAdjacentPage(int direction) {
        int targetPage = currentDesktopPage + direction;
        if (targetPage < 0 || targetPage >= getDesktopPageCount()) {
            if (!loopDesktopPages || getDesktopPageCount() <= 1) {
                Toast.makeText(this, R.string.edit_move_page_edge, Toast.LENGTH_SHORT).show();
                return;
            }
            targetPage = direction > 0 ? 0 : getDesktopPageCount() - 1;
        }
        int selectedAbsolutePosition = currentDesktopPage * getDesktopPageSize() + selectedEditPosition;
        moveAppToPage(selectedAbsolutePosition, targetPage, direction);
        Toast.makeText(this, getString(R.string.edit_move_page_toast, targetPage + 1), Toast.LENGTH_SHORT).show();
    }

    private void animateDesktopPageChange(int direction) {
        animateDesktopPageChange(direction, null);
    }

    private void animateDesktopPageChange(int direction, Bitmap outgoingBitmap) {
        if (desktopGrid == null) {
            recycleBitmap(outgoingBitmap);
            return;
        }
        final int token = ++pageAnimationToken;
        pageAnimationRunning = true;
        desktopGrid.post(() -> {
            if (token != pageAnimationToken) {
                recycleBitmap(outgoingBitmap);
                return;
            }
            resetDesktopTransforms();
            clearDesktopPageOverlays();
            switch (pageAnimation) {
                case PAGE_ANIM_GRID_FLIP:
                    animateRotatePage(direction, outgoingBitmap, token);
                    break;
                case PAGE_ANIM_SHUTTER:
                    animateShutter(direction, outgoingBitmap, token);
                    break;
                case PAGE_ANIM_CUT_CARD:
                    animateCutCard(direction, outgoingBitmap, token);
                    break;
                case PAGE_ANIM_DEFAULT:
                default:
                    animateDefaultSlide(direction, outgoingBitmap, token);
                    break;
            }
            scheduleDesktopPageAnimationCleanup(token, PAGE_ANIMATION_CLEANUP_MS);
        });
    }

    private void animateDefaultSlide(int direction, Bitmap outgoingBitmap, int token) {
        recycleBitmap(outgoingBitmap);
        int distance = Math.max(dp(96), Math.round(desktopGrid.getWidth() * 0.22f));
        desktopGrid.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        desktopGrid.setCameraDistance(dp(9000));
        desktopGrid.setPivotX(direction > 0 ? 0f : desktopGrid.getWidth());
        desktopGrid.setPivotY(desktopGrid.getHeight() * 0.52f);
        desktopGrid.setAlpha(1f);
        desktopGrid.setScaleX(0.982f);
        desktopGrid.setScaleY(0.986f);
        desktopGrid.setRotationY(direction * -3.5f);
        desktopGrid.setTranslationX(direction * distance);
        desktopGrid.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotationY(0f)
                .translationX(0f)
                .setDuration(PAGE_ANIMATION_MS)
                .setInterpolator(originalMotionInterpolator())
                .start();
        animateIncomingCells(direction, 0.88f, dp(18), 8L);
    }

    private void animateCutCard(int direction, Bitmap outgoingBitmap, int token) {
        View outgoingPage = addOutgoingPageOverlay(outgoingBitmap);
        animateOutgoingPage(outgoingPage, outgoingBitmap, direction, 1);
        desktopGrid.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        desktopGrid.setCameraDistance(dp(8000));
        desktopGrid.setPivotX(direction > 0 ? desktopGrid.getWidth() : 0);
        desktopGrid.setPivotY(desktopGrid.getHeight() * 0.52f);
        desktopGrid.setAlpha(0.68f);
        desktopGrid.setScaleX(0.90f);
        desktopGrid.setScaleY(0.94f);
        desktopGrid.setRotationY(direction * -22f);
        desktopGrid.setTranslationX(direction * dp(72));
        desktopGrid.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotationY(0f)
                .translationX(0f)
                .setDuration(PAGE_ANIMATION_MS + 30L)
                .setInterpolator(originalMotionInterpolator())
                .start();
        animateIncomingCells(direction, 0.70f, dp(18), 8L);
    }

    private void animateRotatePage(int direction, Bitmap outgoingBitmap, int token) {
        View outgoingPage = addOutgoingPageOverlay(outgoingBitmap);
        int width = desktopGrid.getWidth() <= 0 ? getResources().getDisplayMetrics().widthPixels : desktopGrid.getWidth();
        int height = desktopGrid.getHeight() <= 0 ? getResources().getDisplayMetrics().heightPixels : desktopGrid.getHeight();
        if (outgoingPage != null) {
            outgoingPage.setCameraDistance(dp(10500));
            outgoingPage.setPivotX(direction > 0 ? width : 0f);
            outgoingPage.setPivotY(height * 0.52f);
            outgoingPage.animate()
                    .alpha(0.04f)
                    .translationX(-direction * width * 0.34f)
                    .scaleX(0.90f)
                    .scaleY(0.94f)
                    .rotationY(direction * 64f)
                    .setDuration(PAGE_ANIMATION_MS + 40L)
                    .setInterpolator(originalMotionInterpolator())
                    .withEndAction(() -> detachOutgoingPageOverlay(outgoingPage, outgoingBitmap))
                    .start();
        } else {
            recycleBitmap(outgoingBitmap);
        }

        desktopGrid.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        desktopGrid.setCameraDistance(dp(10500));
        desktopGrid.setPivotX(direction > 0 ? 0f : width);
        desktopGrid.setPivotY(height * 0.52f);
        desktopGrid.setAlpha(0.50f);
        desktopGrid.setScaleX(0.90f);
        desktopGrid.setScaleY(0.94f);
        desktopGrid.setRotationY(direction * -66f);
        desktopGrid.setTranslationX(direction * width * 0.24f);
        desktopGrid.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .rotationY(0f)
                .translationX(0f)
                .setDuration(PAGE_ANIMATION_MS + 40L)
                .setInterpolator(originalMotionInterpolator())
                .start();
        animateIncomingCells(direction, 0.76f, dp(10), 5L);
    }

    private void animateShutter(int direction, Bitmap outgoingBitmap, int token) {
        View outgoingPage = addOutgoingPageOverlay(outgoingBitmap);
        animateOutgoingPage(outgoingPage, outgoingBitmap, direction, 3);
        desktopGrid.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        cancelDesktopPageAnimator();
        AnimatorSet set = new AnimatorSet();
        desktopPageAnimator = set;
        List<Animator> animators = new ArrayList<>();
        int childCount = desktopGrid.getChildCount();
        int columns = getDesktopColumns();
        for (int i = 0; i < childCount; i++) {
            View child = desktopGrid.getChildAt(i);
            child.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            int row = i / columns;
            boolean reverse = row % 2 == 1;
            child.setCameraDistance(dp(6000));
            child.setPivotX(child.getWidth() * 0.5f);
            child.setPivotY(reverse ? child.getHeight() : 0f);
            child.setAlpha(0.42f);
            child.setRotationX((reverse ? -1f : 1f) * direction * 84f);
            child.setTranslationY((reverse ? 1f : -1f) * dp(10));
            long delay = row * 24L;
            ObjectAnimator rotation = ObjectAnimator.ofFloat(child, View.ROTATION_X, 0f);
            rotation.setStartDelay(delay);
            rotation.setDuration(PAGE_ANIMATION_MS);
            ObjectAnimator alpha = ObjectAnimator.ofFloat(child, View.ALPHA, 1f);
            alpha.setStartDelay(delay);
            alpha.setDuration(PAGE_ANIMATION_MS);
            ObjectAnimator translation = ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, 0f);
            translation.setStartDelay(delay);
            translation.setDuration(PAGE_ANIMATION_MS);
            animators.add(rotation);
            animators.add(alpha);
            animators.add(translation);
        }
        set.playTogether(animators);
        set.setInterpolator(originalMotionInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (desktopPageAnimator == animation) {
                    desktopPageAnimator = null;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (desktopPageAnimator == animation) {
                    desktopPageAnimator = null;
                }
            }
        });
        set.start();
    }

    private Bitmap captureDesktopGridBitmap() {
        if (desktopGrid == null || desktopGrid.getWidth() <= 0 || desktopGrid.getHeight() <= 0) {
            return null;
        }
        try {
            Bitmap bitmap = Bitmap.createBitmap(desktopGrid.getWidth(), desktopGrid.getHeight(), Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            desktopGrid.draw(canvas);
            return bitmap;
        } catch (OutOfMemoryError error) {
            return null;
        }
    }

    private View addOutgoingPageOverlay(Bitmap bitmap) {
        if (bitmap == null || desktopGridLayer == null) {
            recycleBitmap(bitmap);
            return null;
        }
        clearDesktopPageOverlays();

        FrameLayout overlay = new FrameLayout(this);
        overlay.setClipChildren(false);
        overlay.setClipToPadding(false);
        overlay.setTag(bitmap);

        ImageView snapshot = new ImageView(this);
        snapshot.setScaleType(ImageView.ScaleType.FIT_XY);
        snapshot.setImageBitmap(bitmap);
        overlay.addView(snapshot, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        View depthShade = new View(this);
        depthShade.setBackground(new PageSwitchDepthDrawable());
        depthShade.setAlpha(0.42f);
        overlay.addView(depthShade, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        desktopGridLayer.addView(overlay, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        overlay.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        return overlay;
    }

    private boolean shouldCaptureOutgoingPage() {
        return pageAnimation != PAGE_ANIM_DEFAULT;
    }

    private void animateOutgoingPage(View outgoingPage, Bitmap bitmap, int direction, int style) {
        if (outgoingPage == null) {
            recycleBitmap(bitmap);
            return;
        }
        int width = desktopGrid == null || desktopGrid.getWidth() <= 0
                ? getResources().getDisplayMetrics().widthPixels
                : desktopGrid.getWidth();
        int height = desktopGrid == null || desktopGrid.getHeight() <= 0
                ? getResources().getDisplayMetrics().heightPixels
                : desktopGrid.getHeight();
        outgoingPage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        float targetTranslation = -direction * width * (style == 1 ? 0.46f : 0.36f);
        float targetScale = style == 1 ? 0.88f : 0.94f;
        float targetRotation = direction * (style == 2 ? 28f : style == 1 ? 18f : 7f);
        long duration = PAGE_ANIMATION_MS + (style == 1 ? 50L : 20L);

        outgoingPage.setCameraDistance(dp(9000));
        outgoingPage.setPivotX(direction > 0 ? width : 0f);
        outgoingPage.setPivotY(height * 0.52f);
        outgoingPage.animate()
                .alpha(0f)
                .translationX(targetTranslation)
                .scaleX(targetScale)
                .scaleY(style == 3 ? 0.90f : 0.95f)
                .rotationY(targetRotation)
                .setDuration(duration)
                .setInterpolator(originalMotionInterpolator())
                .withEndAction(() -> detachOutgoingPageOverlay(outgoingPage, bitmap))
                .start();
    }

    private void animateIncomingCells(int direction, float initialAlpha, int offset, long delayStep) {
        int childCount = desktopGrid.getChildCount();
        int columns = Math.max(1, getDesktopColumns());
        for (int i = 0; i < childCount; i++) {
            View child = desktopGrid.getChildAt(i);
            int row = i / columns;
            int column = i % columns;
            child.animate().cancel();
            child.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            child.setAlpha(initialAlpha);
            child.setScaleX(0.94f);
            child.setScaleY(0.94f);
            child.setTranslationX(direction * offset * (1f + row * 0.04f));
            child.setTranslationY((row % 2 == 0 ? -1f : 1f) * dp(4));
            long delay = row * 14L + column * delayStep;
            child.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationX(0f)
                    .translationY(0f)
                    .setStartDelay(delay)
                    .setDuration(PAGE_ANIMATION_MS)
                    .setInterpolator(originalMotionInterpolator())
                    .start();
        }
    }

    private TimeInterpolator originalMotionInterpolator() {
        return new PathInterpolator(ORIGINAL_CURVE_X1, ORIGINAL_CURVE_Y1, ORIGINAL_CURVE_X2, ORIGINAL_CURVE_Y2);
    }

    private void scheduleDesktopPageAnimationCleanup(int token, long delayMs) {
        mainHandler.postDelayed(() -> completeDesktopPageAnimation(token), delayMs);
    }

    private void completeDesktopPageAnimation(int token) {
        if (token != pageAnimationToken) {
            return;
        }
        pageAnimationRunning = false;
        resetDesktopTransforms();
        clearDesktopPageOverlays();
    }

    private void clearDesktopPageOverlays() {
        if (desktopGridLayer == null) {
            return;
        }
        for (int i = desktopGridLayer.getChildCount() - 1; i >= 0; i--) {
            View child = desktopGridLayer.getChildAt(i);
            if (child == desktopGrid || child == desktopLoadingOverlay) {
                continue;
            }
            Object tag = child.getTag();
            detachOutgoingPageOverlay(child, tag instanceof Bitmap ? (Bitmap) tag : null);
        }
    }

    private void detachOutgoingPageOverlay(View overlay, Bitmap bitmap) {
        overlay.setLayerType(View.LAYER_TYPE_NONE, null);
        if (overlay instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) overlay;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof ImageView) {
                    ((ImageView) child).setImageDrawable(null);
                }
            }
        }
        if (desktopGridLayer != null && overlay.getParent() == desktopGridLayer) {
            desktopGridLayer.removeView(overlay);
        }
        recycleBitmap(bitmap);
    }

    private void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void cancelDesktopPageAnimation() {
        if (desktopGrid == null) {
            pageAnimationRunning = false;
            pageAnimationToken++;
            clearDesktopPageOverlays();
            return;
        }
        pageAnimationRunning = false;
        pageAnimationToken++;
        resetDesktopTransforms();
        clearDesktopPageOverlays();
    }

    private void cancelDesktopPageAnimator() {
        if (desktopPageAnimator == null) {
            return;
        }
        AnimatorSet animator = desktopPageAnimator;
        desktopPageAnimator = null;
        animator.removeAllListeners();
        animator.cancel();
    }

    private void clearDesktopGridDrawingCaches() {
        if (desktopGrid == null) {
            return;
        }
        clearViewDrawingCache(desktopGrid);
        for (int i = 0; i < desktopGrid.getChildCount(); i++) {
            clearViewDrawingCache(desktopGrid.getChildAt(i));
        }
        if (desktopGridLayer != null) {
            clearViewDrawingCache(desktopGridLayer);
        }
    }

    private void clearViewDrawingCache(View view) {
        if (view == null) {
            return;
        }
        view.clearAnimation();
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.jumpDrawablesToCurrentState();
        }
        view.invalidate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.postInvalidateOnAnimation();
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                child.destroyDrawingCache();
                child.setDrawingCacheEnabled(false);
                child.invalidate();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    child.postInvalidateOnAnimation();
                }
            }
        }
    }

    private void resetDesktopTransforms() {
        cancelDesktopPageAnimator();
        desktopGrid.animate().cancel();
        desktopGrid.clearAnimation();
        desktopGrid.setAlpha(1f);
        desktopGrid.setScaleX(1f);
        desktopGrid.setScaleY(1f);
        desktopGrid.setRotationX(0f);
        desktopGrid.setRotationY(0f);
        desktopGrid.setRotation(0f);
        desktopGrid.setTranslationX(0f);
        desktopGrid.setTranslationY(0f);
        if (desktopGrid.getWidth() > 0) {
            desktopGrid.setPivotX(desktopGrid.getWidth() * 0.5f);
        }
        if (desktopGrid.getHeight() > 0) {
            desktopGrid.setPivotY(desktopGrid.getHeight() * 0.5f);
        }
        desktopGrid.setCameraDistance(dp(9000));
        desktopGrid.setLayerType(View.LAYER_TYPE_NONE, null);
        for (int i = 0; i < desktopGrid.getChildCount(); i++) {
            View child = desktopGrid.getChildAt(i);
            child.animate().cancel();
            child.clearAnimation();
            child.setLayerType(View.LAYER_TYPE_NONE, null);
            child.setCameraDistance(dp(6000));
            child.setAlpha(1f);
            child.setScaleX(1f);
            child.setScaleY(1f);
            child.setRotation(0f);
            child.setRotationX(0f);
            child.setRotationY(0f);
            child.setTranslationX(0f);
            child.setTranslationY(0f);
            if (child.getWidth() > 0) {
                child.setPivotX(child.getWidth() * 0.5f);
            }
            if (child.getHeight() > 0) {
                child.setPivotY(child.getHeight() * 0.55f);
            }
            applyDesktopCellTransforms(child, i, getDesktopApp(i) != null);
        }
        clearDesktopGridDrawingCaches();
    }

    private void enterEditMode() {
        if (lockDesktopLayout) {
            Toast.makeText(this, R.string.desktop_layout_locked_toast, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!editMode) {
            editMode = true;
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            updatePageIndicator();
            performDesktopHaptic(HapticFeedbackConstants.LONG_PRESS);
            animateEditModeChange(true);
            Toast.makeText(this, R.string.edit_mode_toast, Toast.LENGTH_SHORT).show();
        }
    }

    private void exitEditMode() {
        if (editMode) {
            editMode = false;
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            updatePageIndicator();
            animateEditModeChange(false);
        }
    }

    private void handleEditTap(int position) {
        int absolutePosition = currentDesktopPage * getDesktopPageSize() + position;
        if (selectedEditPosition < 0) {
            if (absolutePosition >= desktopApps.size()) {
                selectedEditPosition = -1;
                desktopAdapter.notifyDataSetChanged();
                updateDesktopStatus();
                return;
            }
            selectedEditPosition = position;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            return;
        }
        int selectedAbsolutePosition = currentDesktopPage * getDesktopPageSize() + selectedEditPosition;
        if (selectedAbsolutePosition < desktopApps.size()
                && selectedAbsolutePosition == absolutePosition) {
            AppEntry app = desktopApps.get(selectedAbsolutePosition);
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            performDesktopHaptic(HapticFeedbackConstants.VIRTUAL_KEY);
            showAppActions(app);
            return;
        }
        if (selectedAbsolutePosition < desktopApps.size()
                && absolutePosition >= desktopApps.size()
                && selectedAbsolutePosition != desktopApps.size() - 1) {
            moveDesktopApp(selectedAbsolutePosition, desktopApps.size());
        } else if (selectedAbsolutePosition < desktopApps.size()
                && absolutePosition < desktopApps.size()
                && selectedAbsolutePosition != absolutePosition) {
            Collections.swap(desktopApps, selectedAbsolutePosition, absolutePosition);
            markCustomOrder();
        }
        selectedEditPosition = -1;
        desktopAdapter.notifyDataSetChanged();
        updateDesktopStatus();
    }

    private boolean startDesktopDrag(View view, int pagePosition) {
        if (!editMode || lockDesktopLayout || view == null) {
            return false;
        }
        AppEntry app = getDesktopApp(pagePosition);
        if (app == null) {
            return false;
        }
        int absolutePosition = currentDesktopPage * getDesktopPageSize() + pagePosition;
        selectedEditPosition = pagePosition;
        desktopAdapter.notifyDataSetChanged();
        updateDesktopStatus();

        ClipData data = ClipData.newPlainText("desktop_app", app.label);
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        boolean started;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            started = view.startDragAndDrop(data, shadow, absolutePosition, 0);
        } else {
            started = view.startDrag(data, shadow, absolutePosition, 0);
        }
        if (started) {
            performDesktopHaptic(HapticFeedbackConstants.LONG_PRESS);
        }
        return started;
    }

    private boolean startAllAppsDrag(View view, AppEntry app) {
        if (view == null || app == null) {
            return false;
        }
        pendingAllAppsDropApp = app;
        showAllAppsDropTray(true);
        ClipData data = ClipData.newPlainText("all_apps_app", app.label);
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
        AppDragPayload payload = new AppDragPayload(app);
        boolean started;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            started = view.startDragAndDrop(data, shadow, payload, 0);
        } else {
            started = view.startDrag(data, shadow, payload, 0);
        }
        if (started) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
        }
        return true;
    }

    private void showAllAppsDropTray(boolean show) {
        if (allAppsDropTray == null) {
            return;
        }
        allAppsDropTray.animate().cancel();
        if (show) {
            allAppsDropTray.setVisibility(View.VISIBLE);
            allAppsDropTray.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(160L)
                    .setInterpolator(new DecelerateInterpolator(1.35f))
                    .start();
        } else {
            allAppsDropTray.animate()
                    .alpha(0f)
                    .translationY(dp(24))
                    .setDuration(140L)
                    .setInterpolator(new DecelerateInterpolator(1.1f))
                    .withEndAction(() -> {
                        if (allAppsDropTray != null && allAppsDropTray.getAlpha() <= 0.05f) {
                            allAppsDropTray.setVisibility(View.GONE);
                        }
                    })
                    .start();
        }
    }

    private boolean handleAllAppsDropTargetDrag(View view, DragEvent event, int target) {
        Object state = event.getLocalState();
        boolean appDrag = state instanceof AppDragPayload;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return appDrag;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (appDrag) {
                    view.setBackground(new DropTargetDrawable(true));
                    view.animate().scaleX(1.025f).scaleY(1.025f).setDuration(90L).start();
                }
                return appDrag;
            case DragEvent.ACTION_DRAG_EXITED:
                view.setBackground(new DropTargetDrawable(false));
                view.animate().scaleX(1f).scaleY(1f).setDuration(90L).start();
                return appDrag;
            case DragEvent.ACTION_DROP:
                if (!appDrag) {
                    return false;
                }
                AppDragPayload payload = (AppDragPayload) state;
                payload.handled = true;
                pendingAllAppsDropApp = null;
                performAllAppsDrop(payload.app, target);
                view.setBackground(new DropTargetDrawable(false));
                view.animate().scaleX(1f).scaleY(1f).setDuration(90L).start();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                pendingAllAppsDropApp = null;
                view.setBackground(new DropTargetDrawable(false));
                view.animate().scaleX(1f).scaleY(1f).setDuration(90L).start();
                showAllAppsDropTray(false);
                return appDrag;
            default:
                return appDrag;
        }
    }

    private void performAllAppsDrop(AppEntry app, int target) {
        if (app == null) {
            return;
        }
        if (target == DROP_TARGET_DOCK) {
            setQuickLaunchPinned(app, true);
            Toast.makeText(this, R.string.desktop_app_pinned_dock_toast, Toast.LENGTH_SHORT).show();
        } else {
            addAppToDesktop(app, true);
        }
    }

    private boolean handleDockDropEvent(DragEvent event) {
        Object state = event.getLocalState();
        boolean supported = (editMode && state instanceof Integer) || state instanceof AppDragPayload;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return supported;
            case DragEvent.ACTION_DRAG_ENTERED:
                if (supported && desktopDock != null) {
                    desktopDock.animate().alpha(0.82f).scaleX(0.985f).scaleY(0.985f).setDuration(100L).start();
                }
                return supported;
            case DragEvent.ACTION_DRAG_EXITED:
                resetDockDropTransform();
                return supported;
            case DragEvent.ACTION_DROP:
                AppEntry app = getDraggedApp(state);
                if (app == null) {
                    resetDockDropTransform();
                    return false;
                }
                if (state instanceof AppDragPayload) {
                    ((AppDragPayload) state).handled = true;
                    pendingAllAppsDropApp = null;
                }
                setQuickLaunchPinned(app, true);
                selectedEditPosition = -1;
                if (desktopAdapter != null) {
                    desktopAdapter.notifyDataSetChanged();
                }
                updateDesktopStatus();
                resetDockDropTransform();
                Toast.makeText(this, R.string.desktop_app_pinned_dock_toast, Toast.LENGTH_SHORT).show();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                resetDockDropTransform();
                return supported;
            default:
                return supported;
        }
    }

    private AppEntry getDraggedApp(Object state) {
        if (state instanceof AppDragPayload) {
            return ((AppDragPayload) state).app;
        }
        if (state instanceof Integer) {
            int index = (Integer) state;
            return index >= 0 && index < desktopApps.size() ? desktopApps.get(index) : null;
        }
        return null;
    }

    private void resetDockDropTransform() {
        if (desktopDock == null) {
            return;
        }
        desktopDock.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(100L).start();
    }

    private boolean handleDesktopDragEvent(DragEvent event, int targetPagePosition) {
        Object localState = event.getLocalState();
        boolean desktopDrag = editMode && localState instanceof Integer;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return desktopDrag;
            case DragEvent.ACTION_DROP:
                if (!desktopDrag) {
                    return false;
                }
                handleDesktopDrop((Integer) localState, targetPagePosition);
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult() && selectedEditPosition >= 0) {
                    selectedEditPosition = -1;
                    desktopAdapter.notifyDataSetChanged();
                    updateDesktopStatus();
                }
                return true;
            default:
                return desktopDrag;
        }
    }

    private boolean handlePageIndicatorDragEvent(DragEvent event, int targetPage) {
        Object localState = event.getLocalState();
        boolean desktopDrag = editMode && localState instanceof Integer;
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return desktopDrag;
            case DragEvent.ACTION_DROP:
                if (!desktopDrag) {
                    return false;
                }
                moveAppToPage((Integer) localState, targetPage);
                Toast.makeText(this, getString(R.string.edit_move_page_toast, targetPage + 1), Toast.LENGTH_SHORT).show();
                return true;
            case DragEvent.ACTION_DRAG_ENDED:
                if (!event.getResult() && selectedEditPosition >= 0) {
                    selectedEditPosition = -1;
                    desktopAdapter.notifyDataSetChanged();
                    updateDesktopStatus();
                }
                return true;
            default:
                return desktopDrag;
        }
    }

    private void handleDesktopDrop(int sourceAbsolutePosition, int targetPagePosition) {
        int targetAbsolutePosition = currentDesktopPage * getDesktopPageSize() + targetPagePosition;
        if (sourceAbsolutePosition < 0 || sourceAbsolutePosition >= desktopApps.size()) {
            selectedEditPosition = -1;
            desktopAdapter.notifyDataSetChanged();
            updateDesktopStatus();
            return;
        }
        if (targetAbsolutePosition >= desktopApps.size()) {
            if (sourceAbsolutePosition != desktopApps.size() - 1) {
                moveDesktopApp(sourceAbsolutePosition, desktopApps.size());
            }
        } else if (sourceAbsolutePosition != targetAbsolutePosition) {
            Collections.swap(desktopApps, sourceAbsolutePosition, targetAbsolutePosition);
            markCustomOrder();
        }
        selectedEditPosition = -1;
        desktopAdapter.notifyDataSetChanged();
        updateDesktopStatus();
        performDesktopHaptic(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    private void moveDesktopApp(int fromIndex, int toIndex) {
        if (fromIndex < 0 || fromIndex >= desktopApps.size()) {
            return;
        }
        AppEntry app = desktopApps.remove(fromIndex);
        int targetIndex = Math.max(0, Math.min(toIndex, desktopApps.size()));
        desktopApps.add(targetIndex, app);
        markCustomOrder();
    }

    private void markCustomOrder() {
        sortMode = SORT_CUSTOM;
        preferences.edit().putInt(PREF_SORT_MODE, sortMode).apply();
        saveDesktopOrder();
    }

    private void animateEditModeChange(boolean entering) {
        desktopGrid.post(() -> {
            for (int i = 0; i < desktopGrid.getChildCount(); i++) {
                View child = desktopGrid.getChildAt(i);
                AppEntry app = getDesktopApp(i);
                child.animate().cancel();
                if (entering && app != null) {
                    child.setScaleX(1f);
                    child.setScaleY(1f);
                    child.setRotation(0f);
                    child.setAlpha(1f);
                    child.animate()
                            .scaleX(getEditCellScale(i, true))
                            .scaleY(getEditCellScale(i, true))
                            .rotation(getEditCellRotation(i, true))
                            .alpha(getEditCellAlpha(i, true))
                            .setDuration(180L)
                            .setStartDelay((i % getDesktopColumns()) * 16L)
                            .setInterpolator(new DecelerateInterpolator(1.45f))
                            .start();
                } else {
                    child.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .rotation(0f)
                            .alpha(1f)
                            .setDuration(160L)
                            .setInterpolator(new DecelerateInterpolator(1.25f))
                            .start();
                }
            }
        });
    }

    private void applyDesktopCellTransforms(View cell, int position, boolean hasApp) {
        if (cell == null) {
            return;
        }
        cell.animate().cancel();
        cell.setPivotX(cell.getWidth() * 0.5f);
        cell.setPivotY(cell.getHeight() * 0.55f);
        cell.setScaleX(getEditCellScale(position, hasApp));
        cell.setScaleY(getEditCellScale(position, hasApp));
        cell.setRotation(getEditCellRotation(position, hasApp));
        cell.setAlpha(getEditCellAlpha(position, hasApp));
    }

    private float getEditCellScale(int position, boolean hasApp) {
        if (!editMode || !hasApp) {
            return 1f;
        }
        return selectedEditPosition == position ? 1.035f : 0.965f;
    }

    private float getEditCellRotation(int position, boolean hasApp) {
        if (!editMode || !hasApp || selectedEditPosition == position) {
            return 0f;
        }
        return position % 2 == 0 ? -0.85f : 0.85f;
    }

    private float getEditCellAlpha(int position, boolean hasApp) {
        if (!editMode || !hasApp || selectedEditPosition == position) {
            return 1f;
        }
        return 0.92f;
    }

    private AppEntry getDesktopApp(int pagePosition) {
        int absolutePosition = currentDesktopPage * getDesktopPageSize() + pagePosition;
        return absolutePosition < desktopApps.size() ? desktopApps.get(absolutePosition) : null;
    }

    private int getDesktopPageSize() {
        return getDesktopColumns() * getDesktopRows();
    }

    private int getDesktopColumns() {
        return desktopMode == 20 ? 4 : 3;
    }

    private int getDesktopRows() {
        return desktopMode == 20 ? 5 : 4;
    }

    private int normalizeDesktopMode(int mode) {
        if (mode == 20 || mode == 4) {
            return 20;
        }
        return 12;
    }

    private int getDesktopPageCount() {
        if (desktopApps.isEmpty()) {
            return 1;
        }
        return Math.max(1, (desktopApps.size() + getDesktopPageSize() - 1) / getDesktopPageSize());
    }

    private void clampDesktopPage() {
        int lastPage = getDesktopPageCount() - 1;
        if (currentDesktopPage > lastPage) {
            currentDesktopPage = lastPage;
        }
        if (currentDesktopPage < 0) {
            currentDesktopPage = 0;
        }
    }

    private void handleDialKey(String label) {
        String current = searchBox.getText().toString();
        if ("CALL".equals(label)) {
            dialSearchNumber();
            return;
        }
        if ("DEL".equals(label)) {
            if (!current.isEmpty()) {
                searchBox.setText(current.substring(0, current.length() - 1));
                searchBox.setSelection(searchBox.length());
            }
            return;
        }
        searchBox.append(label.substring(0, 1));
    }

    private void showDesktop() {
        hideKeyboard();
        if (searchView != null && searchView.getVisibility() == View.VISIBLE) {
            setAllAppsAddMode(false);
            animateSearchExit();
            return;
        }
        if (settingsView != null && settingsView.getVisibility() == View.VISIBLE) {
            navigateSettingsPage(SETTINGS_PAGE_MAIN, SETTINGS_PAGE_MAIN);
            animateSettingsExit();
            return;
        }
        viewTransitionToken++;
        viewTransitionRunning = false;
        desktopView.setVisibility(View.VISIBLE);
        if (searchView != null) {
            searchView.setVisibility(View.GONE);
        }
        if (settingsView != null) {
            settingsView.setVisibility(View.GONE);
        }
        navigateSettingsPage(SETTINGS_PAGE_MAIN, SETTINGS_PAGE_MAIN);
        configureSystemBars(false);
        resetViewTransforms(desktopView);
        resetViewTransforms(searchView);
        resetViewTransforms(settingsView);
    }

    private void showSearch() {
        if (viewTransitionRunning || (searchView != null && searchView.getVisibility() == View.VISIBLE)) {
            return;
        }
        ensureSearchView();
        cancelDesktopPageAnimation();
        setAllAppsAddMode(false);
        if (!appsLoaded && !appsLoading) {
            loadApps();
        }
        exitEditMode();
        configureSystemBars(true);
        if (settingsView != null) {
            settingsView.setVisibility(View.GONE);
        }
        desktopView.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.VISIBLE);
        searchView.requestFocus();
        searchBox.clearFocus();
        filterApps(searchBox.getText().toString());
        hideKeyboard();
        animateSearchEnter();
    }

    private void setAllAppsAddMode(boolean addMode) {
        allAppsAddMode = addMode;
        updateAllAppsModeUi();
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    private void updateAllAppsModeUi() {
        if (allAppsTitle != null) {
            allAppsTitle.setText(allAppsAddMode ? R.string.add_apps_title : R.string.all_apps_launcher_title);
        }
        if (allAppsActionButton != null) {
            allAppsActionButton.setText(allAppsAddMode ? R.string.batch_add : R.string.add_apps_action);
        }
    }

    private boolean handleAllAppsPullHomeGesture(MotionEvent event) {
        if (!allAppsAddMode || allAppsListView == null) {
            if (allAppsPullHomeCandidate || allAppsPullHomeActive) {
                resetAllAppsPullHomeFeedback();
            }
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                allAppsDownY = event.getRawY();
                allAppsDownX = event.getRawX();
                allAppsPullHomeCandidate = isAllAppsListAtTop();
                allAppsPullHomeActive = false;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (!allAppsPullHomeCandidate || !isAllAppsListAtTop()) {
                    return false;
                }
                float moveDeltaY = event.getRawY() - allAppsDownY;
                float moveDeltaX = Math.abs(event.getRawX() - allAppsDownX);
                if (moveDeltaY > dp(10) && moveDeltaY > moveDeltaX * 1.15f) {
                    allAppsPullHomeActive = true;
                    applyAllAppsPullHomeFeedback(moveDeltaY);
                }
                return false;
            case MotionEvent.ACTION_UP:
                float deltaY = event.getRawY() - allAppsDownY;
                float deltaX = Math.abs(event.getRawX() - allAppsDownX);
                boolean shouldReturnHome = allAppsPullHomeCandidate
                        && allAppsPullHomeActive
                        && deltaY > Math.max(dp(74), allAppsListView.getHeight() * 0.095f)
                        && deltaX < deltaY * 0.72f;
                resetAllAppsPullHomeFeedback();
                if (shouldReturnHome) {
                    setAllAppsAddMode(false);
                    showDesktop();
                    return true;
                }
                return false;
            case MotionEvent.ACTION_CANCEL:
                resetAllAppsPullHomeFeedback();
                return false;
            default:
                return false;
        }
    }

    private boolean isAllAppsListAtTop() {
        if (allAppsListView == null) {
            return false;
        }
        if (allAppsListView.getFirstVisiblePosition() > 0) {
            return false;
        }
        if (allAppsListView.getChildCount() == 0) {
            return true;
        }
        View firstChild = allAppsListView.getChildAt(0);
        return firstChild == null || firstChild.getTop() >= allAppsListView.getPaddingTop();
    }

    private void applyAllAppsPullHomeFeedback(float distance) {
        if (allAppsListLayer == null) {
            return;
        }
        float progress = Math.min(1f, distance / Math.max(1f, dp(128)));
        allAppsListLayer.setTranslationY(dp(46) * progress);
        allAppsListLayer.setAlpha(1f - 0.10f * progress);
    }

    private void resetAllAppsPullHomeFeedback() {
        allAppsPullHomeCandidate = false;
        allAppsPullHomeActive = false;
        if (allAppsListLayer == null) {
            return;
        }
        allAppsListLayer.animate().cancel();
        allAppsListLayer.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(150L)
                .setInterpolator(new DecelerateInterpolator(1.25f))
                .start();
    }

    private void animateSearchEnter() {
        viewTransitionRunning = true;
        final int token = ++viewTransitionToken;
        searchView.animate().cancel();
        desktopView.animate().cancel();
        resetViewTransforms(searchView);
        resetViewTransforms(desktopView);

        int distance = root.getHeight() > 0 ? root.getHeight() : getResources().getDisplayMetrics().heightPixels;
        searchView.setAlpha(0.92f);
        searchView.setTranslationY(Math.max(dp(220), distance * 0.42f));
        searchView.setScaleX(0.985f);
        searchView.setScaleY(0.985f);
        desktopView.setAlpha(1f);
        desktopView.setScaleX(1f);
        desktopView.setScaleY(1f);

        desktopView.animate()
                .alpha(0.35f)
                .scaleX(0.965f)
                .scaleY(0.965f)
                .translationY(-dp(18))
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.2f))
                .start();
        searchView.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.45f))
                .withEndAction(() -> {
                    if (token != viewTransitionToken) {
                        return;
                    }
                    desktopView.setVisibility(View.GONE);
                    resetViewTransforms(desktopView);
                    resetViewTransforms(searchView);
                    viewTransitionRunning = false;
                })
                .start();
    }

    private void animateSearchExit() {
        viewTransitionRunning = true;
        final int token = ++viewTransitionToken;
        configureSystemBars(false);
        searchView.animate().cancel();
        desktopView.animate().cancel();
        desktopView.setVisibility(View.VISIBLE);
        if (settingsView != null) {
            settingsView.setVisibility(View.GONE);
        }
        resetViewTransforms(searchView);
        resetViewTransforms(desktopView);

        desktopView.setAlpha(0.35f);
        desktopView.setScaleX(0.965f);
        desktopView.setScaleY(0.965f);
        desktopView.setTranslationY(-dp(18));

        int distance = root.getHeight() > 0 ? root.getHeight() : getResources().getDisplayMetrics().heightPixels;
        searchView.animate()
                .alpha(0.88f)
                .translationY(Math.max(dp(220), distance * 0.38f))
                .scaleX(0.985f)
                .scaleY(0.985f)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.1f))
                .withEndAction(() -> {
                    if (token != viewTransitionToken) {
                        return;
                    }
                    searchView.setVisibility(View.GONE);
                    resetViewTransforms(searchView);
                    viewTransitionRunning = false;
                })
                .start();
        desktopView.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.35f))
                .start();
    }

    private void showKeyboardForSearch() {
        if (searchBox == null) {
            return;
        }
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(searchBox, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void showSettings() {
        if (settingsView != null && settingsView.getVisibility() == View.VISIBLE) {
            return;
        }
        if (settingsPage != SETTINGS_PAGE_MAIN) {
            navigateSettingsPage(SETTINGS_PAGE_MAIN, SETTINGS_PAGE_MAIN);
            refreshSettingsView();
        }
        ensureSettingsView();
        exitEditMode();
        cancelDesktopPageAnimation();
        hideKeyboard();
        viewTransitionRunning = false;
        viewTransitionToken++;
        desktopView.animate().cancel();
        if (searchView != null) {
            searchView.animate().cancel();
        }
        settingsView.animate().cancel();
        configureSystemBars(true);
        desktopView.setVisibility(View.VISIBLE);
        if (searchView != null) {
            searchView.setVisibility(View.GONE);
        }
        settingsView.setVisibility(View.VISIBLE);
        resetViewTransforms(desktopView);
        resetViewTransforms(searchView);
        resetViewTransforms(settingsView);
        settingsView.bringToFront();
        animateSettingsEnter();
    }

    private void animateSettingsEnter() {
        if (settingsView == null) {
            viewTransitionRunning = false;
            return;
        }
        viewTransitionRunning = true;
        final int token = ++viewTransitionToken;
        desktopView.animate().cancel();
        settingsView.animate().cancel();
        resetViewTransforms(desktopView);
        resetViewTransforms(settingsView);

        int distance = root.getHeight() > 0 ? root.getHeight() : getResources().getDisplayMetrics().heightPixels;
        float offset = Math.max(dp(86), distance * 0.10f);
        settingsView.setAlpha(0.98f);
        settingsView.setTranslationY(offset);
        desktopView.setAlpha(1f);
        desktopView.setScaleX(1f);
        desktopView.setScaleY(1f);

        desktopView.animate()
                .alpha(0.28f)
                .scaleX(0.972f)
                .scaleY(0.972f)
                .translationY(-dp(14))
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.25f))
                .start();
        settingsView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.35f))
                .withEndAction(() -> {
                    if (token != viewTransitionToken) {
                        return;
                    }
                    desktopView.setVisibility(View.GONE);
                    resetViewTransforms(desktopView);
                    resetViewTransforms(settingsView);
                    viewTransitionRunning = false;
                })
                .start();
    }

    private void animateSettingsExit() {
        if (settingsView == null) {
            viewTransitionRunning = false;
            return;
        }
        viewTransitionRunning = true;
        final int token = ++viewTransitionToken;
        configureSystemBars(false);
        desktopView.animate().cancel();
        settingsView.animate().cancel();
        desktopView.setVisibility(View.VISIBLE);
        resetViewTransforms(desktopView);
        resetViewTransforms(settingsView);

        desktopView.setAlpha(0.28f);
        desktopView.setScaleX(0.972f);
        desktopView.setScaleY(0.972f);
        desktopView.setTranslationY(-dp(14));
        float offset = Math.max(dp(86), (root.getHeight() > 0 ? root.getHeight() : getResources().getDisplayMetrics().heightPixels) * 0.10f);
        settingsView.animate()
                .alpha(0.96f)
                .translationY(offset)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.1f))
                .withEndAction(() -> {
                    if (token != viewTransitionToken) {
                        return;
                    }
                    settingsView.setVisibility(View.GONE);
                    resetViewTransforms(settingsView);
                    viewTransitionRunning = false;
                })
                .start();
        desktopView.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(VIEW_TRANSITION_MS)
                .setInterpolator(new DecelerateInterpolator(1.35f))
                .start();
    }

    private void ensureSearchView() {
        if (searchView != null) {
            return;
        }
        searchView = createSearchView();
        searchView.setVisibility(View.GONE);
        root.addView(searchView);
    }

    private void ensureSettingsView() {
        if (settingsView != null) {
            return;
        }
        settingsView = createSettingsView();
        settingsView.setVisibility(View.GONE);
        root.addView(settingsView);
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && searchBox != null) {
            inputMethodManager.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        }
    }

    private void resetViewTransforms(View view) {
        if (view == null) {
            return;
        }
        view.animate().cancel();
        view.clearAnimation();
        view.setAlpha(1f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.setRotationX(0f);
        view.setRotationY(0f);
        view.setRotation(0f);
        view.setTranslationX(0f);
        view.setTranslationY(0f);
    }

    private void loadApps() {
        loadApps(0);
    }

    private void refreshAppsForCurrentSurface() {
        if (searchView != null && searchView.getVisibility() == View.VISIBLE) {
            loadApps();
        } else {
            loadHomeApps();
        }
    }

    private void loadHomeApps() {
        desktopLoadingDismissed = true;
        mainHandler.removeCallbacks(desktopLoadingDismissRunnable);
        updateDesktopLoadingState();
        List<String> desktopKeys = getDesktopAppKeys();
        if (desktopKeys.isEmpty()) {
            appLoadToken++;
            desktopApps.clear();
            refreshPreferenceKeyCaches();
            currentDesktopPage = 0;
            selectedEditPosition = -1;
            if (desktopAdapter != null) {
                desktopAdapter.notifyDataSetChanged();
            }
            updateDesktopStatus();
            updatePageIndicator();
            updateDesktopQuickLaunch();
            refreshSettingsIfVisible();
            packageListDirty = false;
            return;
        }
        final int token = ++appLoadToken;
        appExecutor.execute(() -> {
            if (activityDestroyed) {
                return;
            }
            List<AppEntry> selectedApps = queryAppsForKeys(desktopKeys);
            mainHandler.post(() -> {
                if (activityDestroyed || token != appLoadToken) {
                    return;
                }
                desktopApps.clear();
                desktopApps.addAll(selectedApps);
                refreshPreferenceKeyCaches();
                applySortModeToDesktop();
                clampDesktopPage();
                selectedEditPosition = -1;
                if (desktopAdapter != null) {
                    desktopAdapter.notifyDataSetChanged();
                }
                updateDesktopStatus();
                updatePageIndicator();
                updateDesktopQuickLaunch();
                refreshSettingsIfVisible();
                packageListDirty = false;
            });
        });
    }

    private void loadApps(int completionToastRes) {
        if (appsLoading) {
            return;
        }
        appsLoading = true;
        if (desktopStatus != null) {
            desktopStatus.setText(R.string.loading_apps);
        }
        final int token = ++appLoadToken;
        appExecutor.execute(() -> {
            if (activityDestroyed) {
                return;
            }
            List<AppEntry> apps = queryLaunchableApps();
            mainHandler.post(() -> {
                if (activityDestroyed) {
                    return;
                }
                if (token != appLoadToken) {
                    appsLoading = false;
                    return;
                }
                appsLoaded = true;
                appsLoading = false;
                desktopLoadingDismissed = true;
                allApps.clear();
                allApps.addAll(apps);
                desktopApps.clear();
                desktopApps.addAll(applySavedDesktopSelection(getVisibleApps(apps)));
                refreshPreferenceKeyCaches();
                applySortModeToDesktop();
                clampDesktopPage();
                if (searchView != null) {
                    filterApps(searchBox == null ? "" : searchBox.getText().toString());
                } else {
                    filteredApps.clear();
                }
                if (desktopAdapter != null) {
                    desktopAdapter.notifyDataSetChanged();
                }
                updateDesktopStatus();
                updatePageIndicator();
                updateDesktopLoadingState();
                updateDesktopQuickLaunch();
                refreshSettingsIfVisible();
                packageListDirty = false;
                if (completionToastRes != 0) {
                    Toast.makeText(this, completionToastRes, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private List<AppEntry> queryAppsForKeys(List<String> keys) {
        List<AppEntry> apps = new ArrayList<>();
        if (keys == null || keys.isEmpty()) {
            return apps;
        }
        Set<String> hiddenKeys = new HashSet<>(hiddenAppKeySet);
        Set<String> used = new HashSet<>();
        for (String key : keys) {
            if (TextUtils.isEmpty(key) || hiddenKeys.contains(key) || !used.add(key)) {
                continue;
            }
            AppEntry app = resolveAppEntryByKey(key);
            if (app != null) {
                apps.add(app);
            }
        }
        return apps;
    }

    private AppEntry resolveAppEntryByKey(String key) {
        ComponentName componentName = parseAppKey(key);
        if (componentName == null) {
            return null;
        }
        AppEntry cached = getCachedAppEntry(key);
        if (cached != null) {
            ensureAppIcon(cached);
            return cached;
        }
        PackageManager packageManager = getPackageManager();
        try {
            ActivityInfo activityInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityInfo = packageManager.getActivityInfo(componentName, PackageManager.ComponentInfoFlags.of(0));
            } else {
                activityInfo = packageManager.getActivityInfo(componentName, 0);
            }
            CharSequence loadedLabel = loadActivityLabelSafely(activityInfo, packageManager);
            String label = loadedLabel == null ? componentName.getPackageName() : loadedLabel.toString();
            if (TextUtils.isEmpty(label)) {
                label = componentName.getPackageName();
            }
            return cacheAppEntry(new AppEntry(
                    label,
                    componentName.getPackageName(),
                    componentName,
                    loadActivityIconSafely(activityInfo, packageManager),
                    key,
                    0L
            ));
        } catch (PackageManager.NameNotFoundException | RuntimeException exception) {
            return null;
        }
    }

    private AppEntry getCachedAppEntry(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        synchronized (appEntryCache) {
            return appEntryCache.get(key);
        }
    }

    private AppEntry cacheAppEntry(AppEntry app) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return app;
        }
        synchronized (appEntryCache) {
            AppEntry cached = appEntryCache.get(app.key);
            if (cached != null) {
                if (cached.icon == null && app.icon != null) {
                    cached.icon = app.icon;
                    cached.iconLoadFailed = false;
                }
                return cached;
            }
            appEntryCache.put(app.key, app);
            return app;
        }
    }

    private Drawable ensureAppIcon(AppEntry app) {
        if (app == null) {
            return null;
        }
        if (app.icon != null) {
            return app.icon;
        }
        app.icon = loadAppIcon(app);
        return app.icon;
    }

    private Drawable loadAppIcon(AppEntry app) {
        if (app == null) {
            return null;
        }
        try {
            PackageManager packageManager = getPackageManager();
            ActivityInfo activityInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                activityInfo = packageManager.getActivityInfo(app.componentName, PackageManager.ComponentInfoFlags.of(0));
            } else {
                activityInfo = packageManager.getActivityInfo(app.componentName, 0);
            }
            return loadActivityIconSafely(activityInfo, packageManager);
        } catch (PackageManager.NameNotFoundException | RuntimeException exception) {
            return null;
        }
    }

    private CharSequence loadActivityLabelSafely(ActivityInfo activityInfo, PackageManager packageManager) {
        if (activityInfo == null) {
            return null;
        }
        try {
            return activityInfo.loadLabel(packageManager);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private Drawable loadActivityIconSafely(ActivityInfo activityInfo, PackageManager packageManager) {
        if (activityInfo == null) {
            return null;
        }
        try {
            return activityInfo.loadIcon(packageManager);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private ComponentName parseAppKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        ComponentName componentName = ComponentName.unflattenFromString(key);
        if (componentName != null) {
            return componentName;
        }
        int slash = key.indexOf('/');
        if (slash <= 0 || slash >= key.length() - 1) {
            return null;
        }
        return new ComponentName(key.substring(0, slash), key.substring(slash + 1));
    }

    private List<AppEntry> queryLaunchableApps() {
        PackageManager packageManager = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolved;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resolved = packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0));
            } else {
                resolved = packageManager.queryIntentActivities(intent, 0);
            }
        } catch (RuntimeException exception) {
            resolved = Collections.emptyList();
        }

        List<AppEntry> apps = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (ResolveInfo info : resolved) {
            if (info.activityInfo == null) {
                continue;
            }

            String packageName = info.activityInfo.packageName;
            String className = info.activityInfo.name;
            String key = packageName + "/" + className;
            if (!seen.add(key)) {
                continue;
            }

            CharSequence loadedLabel = loadResolveLabelSafely(info, packageManager);
            String label = loadedLabel == null ? packageName : loadedLabel.toString();
            if (TextUtils.isEmpty(label)) {
                label = packageName;
            }

            apps.add(cacheAppEntry(new AppEntry(
                    label,
                    packageName,
                    new ComponentName(packageName, className),
                    null,
                    key,
                    0L
            )));
        }

        Collator collator = Collator.getInstance(Locale.getDefault());
        Collections.sort(apps, (left, right) -> {
            int compare = collator.compare(left.label, right.label);
            if (compare != 0) {
                return compare;
            }
            return left.packageName.compareTo(right.packageName);
        });
        return apps;
    }

    private CharSequence loadResolveLabelSafely(ResolveInfo info, PackageManager packageManager) {
        if (info == null) {
            return null;
        }
        try {
            return info.loadLabel(packageManager);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private void updateDesktopLoadingState() {
        if (desktopLoadingOverlay == null) {
            return;
        }
        boolean show = !desktopLoadingDismissed && !appsLoaded;
        desktopLoadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        if (desktopLoadingText != null) {
            desktopLoadingText.setText(R.string.loading_home);
        }
        mainHandler.removeCallbacks(loadingAnimationRunnable);
        if (show) {
            mainHandler.post(loadingAnimationRunnable);
        } else if (desktopLoadingIcon != null) {
            desktopLoadingIcon.setRotation(0f);
            desktopLoadingIcon.setAlpha(1f);
            desktopLoadingIcon.setScaleX(1f);
            desktopLoadingIcon.setScaleY(1f);
            if (desktopLoadingDrawable != null) {
                desktopLoadingDrawable.setPhase(0f);
            }
        }
    }

    private long getFirstInstallTime(PackageManager packageManager, String packageName) {
        Long cached = firstInstallTimeCache.get(packageName);
        if (cached != null) {
            return cached;
        }
        long firstInstallTime = 0L;
        try {
            PackageInfo packageInfo;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageInfo = packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0));
            } else {
                packageInfo = packageManager.getPackageInfo(packageName, 0);
            }
            firstInstallTime = packageInfo.firstInstallTime;
        } catch (PackageManager.NameNotFoundException | RuntimeException exception) {
            firstInstallTime = 0L;
        }
        firstInstallTimeCache.put(packageName, firstInstallTime);
        return firstInstallTime;
    }

    private long ensureFirstInstallTime(AppEntry app) {
        if (app == null) {
            return 0L;
        }
        if (app.firstInstallTime == 0L) {
            app.firstInstallTime = getFirstInstallTime(getPackageManager(), app.packageName);
        }
        return app.firstInstallTime;
    }

    private List<AppEntry> applySavedDesktopSelection(List<AppEntry> apps) {
        List<String> selectedKeys = getDesktopAppKeys();
        if (selectedKeys.isEmpty()) {
            return new ArrayList<>();
        }
        Map<String, AppEntry> appsByKey = new HashMap<>();
        for (AppEntry app : apps) {
            appsByKey.put(app.key, app);
        }
        List<AppEntry> ordered = new ArrayList<>();
        Set<String> used = new HashSet<>();
        Set<String> selected = new HashSet<>(selectedKeys);
        String savedOrder = preferences.getString(PREF_APP_ORDER, "");
        String[] keys = savedOrder.split("\\n");
        for (String key : keys) {
            if (TextUtils.isEmpty(key)) {
                continue;
            }
            AppEntry app = appsByKey.get(key);
            if (app != null && selected.contains(app.key) && used.add(app.key)) {
                ordered.add(app);
            }
        }
        for (String key : selectedKeys) {
            if (used.contains(key)) {
                continue;
            }
            AppEntry app = appsByKey.get(key);
            if (app != null && used.add(app.key)) {
                ordered.add(app);
            }
        }
        return ordered;
    }

    private List<String> getDesktopAppKeys() {
        return parsePreferenceKeys(PREF_DESKTOP_APPS, 0);
    }

    private boolean isAppOnDesktop(AppEntry app) {
        return app != null && desktopAppKeySet.contains(app.key);
    }

    private boolean addAppToDesktop(AppEntry app, boolean showToast) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return false;
        }
        if (isAppOnDesktop(app)) {
            if (showToast) {
                Toast.makeText(this, R.string.desktop_app_exists_toast, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        desktopApps.add(app);
        sortMode = SORT_CUSTOM;
        preferences.edit().putInt(PREF_SORT_MODE, sortMode).apply();
        persistDesktopSelectionAndRefresh();
        if (showToast) {
            Toast.makeText(this, R.string.desktop_app_added_toast, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private boolean removeAppFromDesktop(AppEntry app, boolean showToast) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return false;
        }
        boolean removed = false;
        for (int i = desktopApps.size() - 1; i >= 0; i--) {
            if (desktopApps.get(i).key.equals(app.key)) {
                desktopApps.remove(i);
                removed = true;
            }
        }
        if (!removed && !getDesktopAppKeys().contains(app.key)) {
            return false;
        }
        persistDesktopSelectionAndRefresh();
        if (showToast) {
            Toast.makeText(this, R.string.desktop_app_removed_toast, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void persistDesktopSelectionAndRefresh() {
        saveDesktopOrder();
        refreshPreferenceKeyCaches();
        clampDesktopPage();
        selectedEditPosition = -1;
        if (desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
        updateDesktopStatus();
        updatePageIndicator();
    }

    private List<AppEntry> getVisibleApps(List<AppEntry> apps) {
        List<AppEntry> visibleApps = new ArrayList<>();
        Set<String> hiddenKeys = new HashSet<>(hiddenAppKeySet);
        for (AppEntry app : apps) {
            if (!isAppHidden(app, hiddenKeys)) {
                visibleApps.add(app);
            }
        }
        return visibleApps;
    }

    private boolean isAppHidden(AppEntry app) {
        return isAppHidden(app, hiddenAppKeySet);
    }

    private boolean isAppHidden(AppEntry app, Set<String> hiddenKeys) {
        return app != null && hiddenKeys.contains(app.key);
    }

    private Set<String> getHiddenAppKeys() {
        return new HashSet<>(hiddenAppKeySet);
    }

    private String getHiddenAppsSummary() {
        int count = getHiddenAppKeys().size();
        return count == 0
                ? getString(R.string.hidden_apps_empty)
                : getString(R.string.hidden_apps_count_format, count);
    }

    private void showHiddenAppsChooser() {
        navigateSettingsPage(SETTINGS_PAGE_HIDDEN_APPS, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private List<AppEntry> getHiddenApps() {
        Set<String> hiddenKeys = getHiddenAppKeys();
        List<AppEntry> hiddenApps = new ArrayList<>();
        for (AppEntry app : allApps) {
            if (hiddenKeys.contains(app.key)) {
                hiddenApps.add(app);
            }
        }
        Collator collator = Collator.getInstance(Locale.getDefault());
        Collections.sort(hiddenApps, (left, right) -> {
            int compare = collator.compare(left.label, right.label);
            if (compare != 0) {
                return compare;
            }
            return left.packageName.compareTo(right.packageName);
        });
        return hiddenApps;
    }

    private void saveDesktopOrder() {
        StringBuilder builder = new StringBuilder();
        for (AppEntry app : desktopApps) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(app.key);
        }
        preferences.edit()
                .putString(PREF_DESKTOP_APPS, builder.toString())
                .putString(PREF_APP_ORDER, builder.toString())
                .apply();
        refreshPreferenceKeyCaches();
    }

    private void applySortModeToDesktop() {
        if (sortMode == SORT_CUSTOM) {
            return;
        }
        Collator collator = Collator.getInstance(Locale.getDefault());
        if (sortMode == SORT_NAME) {
            Collections.sort(desktopApps, (left, right) -> {
                int compare = collator.compare(left.label, right.label);
                if (compare != 0) {
                    return compare;
                }
                return left.packageName.compareTo(right.packageName);
            });
        } else if (sortMode == SORT_INSTALL_TIME) {
            Collections.sort(desktopApps, (left, right) -> {
                int compare = Long.compare(ensureFirstInstallTime(right), ensureFirstInstallTime(left));
                if (compare != 0) {
                    return compare;
                }
                return collator.compare(left.label, right.label);
            });
        }
        currentDesktopPage = 0;
        selectedEditPosition = -1;
        if (desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        updateDesktopStatus();
        updatePageIndicator();
    }

    private void filterApps(String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());
        Set<String> hiddenKeys = new HashSet<>(hiddenAppKeySet);

        filteredApps.clear();
        if (normalizedQuery.isEmpty()) {
            for (AppEntry app : allApps) {
                if (!isAppHidden(app, hiddenKeys)) {
                    filteredApps.add(app);
                }
            }
        } else {
            for (AppEntry app : allApps) {
                if (isAppHidden(app, hiddenKeys)) {
                    continue;
                }
                String label = app.label.toLowerCase(Locale.getDefault());
                String packageName = app.packageName.toLowerCase(Locale.US);
                String initialKey = getAppInitialSearchKey(app).toLowerCase(Locale.US);
                if (label.contains(normalizedQuery)
                        || packageName.contains(normalizedQuery)
                        || initialKey.contains(normalizedQuery)) {
                    filteredApps.add(app);
                }
            }
            sortSearchResults(normalizedQuery);
        }

        if (searchAdapter != null) {
            searchAdapter.notifyDataSetChanged();
        }
        if (allAppsListView != null) {
            allAppsListView.setSelection(0);
        }
        updateSearchStatus();
        updateRecentAppsPanel(normalizedQuery.isEmpty());
        updateSearchHistoryPanel(normalizedQuery.isEmpty());
    }

    private String[] getFastIndexLetters() {
        return new String[]{
                "A", "B", "C", "D", "E", "F", "G", "H", "I",
                "J", "K", "L", "M", "N", "O", "P", "Q", "R",
                "S", "T", "U", "V", "W", "X", "Y", "Z"
        };
    }

    private void handleAlphabetIndexTouch(View view, MotionEvent event) {
        String[] letters = getFastIndexLetters();
        if (letters.length == 0 || view.getHeight() <= 0) {
            return;
        }
        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL) {
            mainHandler.removeCallbacks(alphabetIndexResetRunnable);
            mainHandler.postDelayed(alphabetIndexResetRunnable, 90L);
            return;
        }
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mainHandler.removeCallbacks(alphabetIndexResetRunnable);
        }
        int verticalPadding = view.getPaddingTop() + view.getPaddingBottom();
        float usableHeight = Math.max(1f, view.getHeight() - verticalPadding);
        float y = event.getY() - view.getPaddingTop();
        int index = (int) (y / Math.max(1f, usableHeight / (float) letters.length));
        index = Math.max(0, Math.min(letters.length - 1, index));
        setActiveAlphabetIndex(index, true);
        scrollAllAppsToLetterRealtime(letters[index]);
        if (action == MotionEvent.ACTION_UP) {
            mainHandler.removeCallbacks(alphabetIndexResetRunnable);
            mainHandler.postDelayed(alphabetIndexResetRunnable, 160L);
        }
    }

    private void scrollAllAppsToLetterRealtime(String letter) {
        if (TextUtils.equals(lastAlphabetScrollLetter, letter)) {
            return;
        }
        lastAlphabetScrollLetter = letter;
        scrollAllAppsToLetter(letter);
    }

    private void setActiveAlphabetIndex(int index, boolean animated) {
        if (activeAlphabetIndex == index && index >= 0) {
            return;
        }
        int previous = activeAlphabetIndex;
        activeAlphabetIndex = index;
        updateAlphabetIndexItem(previous, false, animated);
        updateAlphabetIndexItem(index, true, animated);
        if (index < 0) {
            lastAlphabetScrollLetter = "";
        }
    }

    private void updateAlphabetIndexItem(int index, boolean active, boolean animated) {
        if (index < 0 || index >= alphabetIndexItems.size()) {
            return;
        }
        TextView item = alphabetIndexItems.get(index);
        item.animate().cancel();
        item.setTextColor(active ? Color.rgb(42, 48, 54) : Color.rgb(92, 98, 104));
        item.setAlpha(1f);
        float targetScale = active ? 1.72f : 1f;
        if (animated) {
            item.animate()
                    .scaleX(targetScale)
                    .scaleY(targetScale)
                    .setDuration(active ? 95L : 130L)
                    .setInterpolator(new DecelerateInterpolator(1.6f))
                    .start();
        } else {
            item.setScaleX(targetScale);
            item.setScaleY(targetScale);
        }
    }

    private void scrollAllAppsToLetter(String letter) {
        if (allAppsListView == null || TextUtils.isEmpty(letter)) {
            return;
        }
        int target = -1;
        for (int i = 0; i < filteredApps.size(); i++) {
            if (letter.equals(getAppIndexLetter(filteredApps.get(i)))) {
                target = i;
                break;
            }
        }
        if (target < 0) {
            for (int i = 0; i < filteredApps.size(); i++) {
                if (letter.compareTo(getAppIndexLetter(filteredApps.get(i))) <= 0) {
                    target = i;
                    break;
                }
            }
        }
        if (target >= 0) {
            allAppsListView.setSelection(target);
        }
    }

    private String getAppIndexLetter(AppEntry app) {
        if (app == null) {
            return "#";
        }
        String labelLetter = getFirstIndexLetter(app.label);
        if (!TextUtils.isEmpty(labelLetter)) {
            return labelLetter;
        }
        String packageName = app.packageName == null ? "" : app.packageName.trim();
        if (!TextUtils.isEmpty(packageName)) {
            char first = packageName.charAt(0);
            if ((first >= 'a' && first <= 'z') || (first >= 'A' && first <= 'Z')) {
                return String.valueOf(Character.toUpperCase(first));
            }
        }
        return "#";
    }

    private String getFirstIndexLetter(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        String trimmed = text.trim();
        for (int i = 0; i < trimmed.length(); i++) {
            char value = trimmed.charAt(i);
            if (isAsciiLetter(value)) {
                return String.valueOf(Character.toUpperCase(value));
            }
            char chineseInitial = getChineseInitial(value);
            if (chineseInitial != 0) {
                return String.valueOf(chineseInitial);
            }
        }
        return "";
    }

    private String getAppInitialSearchKey(AppEntry app) {
        if (app == null || TextUtils.isEmpty(app.label)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        String label = app.label.trim();
        for (int i = 0; i < label.length(); i++) {
            char value = label.charAt(i);
            if (isAsciiLetter(value)) {
                builder.append(Character.toUpperCase(value));
                continue;
            }
            char chineseInitial = getChineseInitial(value);
            if (chineseInitial != 0) {
                builder.append(chineseInitial);
            }
        }
        return builder.toString();
    }

    private static boolean isAsciiLetter(char value) {
        return (value >= 'a' && value <= 'z') || (value >= 'A' && value <= 'Z');
    }

    private static char getChineseInitial(char value) {
        if (!isCjkCharacter(value)) {
            return 0;
        }
        String text = String.valueOf(value);
        for (int i = CHINESE_INITIAL_BOUNDARIES.length - 1; i >= 0; i--) {
            if (CHINESE_INITIAL_COLLATOR.compare(text, CHINESE_INITIAL_BOUNDARIES[i]) >= 0) {
                return CHINESE_INITIAL_LETTERS[i];
            }
        }
        return 0;
    }

    private static boolean isCjkCharacter(char value) {
        return (value >= '\u4E00' && value <= '\u9FFF')
                || (value >= '\u3400' && value <= '\u4DBF');
    }

    private void showBatchAddDialog() {
        String query = searchBox == null ? "" : searchBox.getText().toString().trim();
        List<AppEntry> candidates = TextUtils.isEmpty(query)
                ? getVisibleApps(allApps)
                : new ArrayList<>(filteredApps);
        if (candidates.isEmpty()) {
            Toast.makeText(this, appsLoaded ? R.string.no_results : R.string.loading_apps, Toast.LENGTH_SHORT).show();
            return;
        }
        String[] labels = new String[candidates.size()];
        boolean[] checked = new boolean[candidates.size()];
        for (int i = 0; i < candidates.size(); i++) {
            AppEntry app = candidates.get(i);
            labels[i] = app.label;
            checked[i] = isAppOnDesktop(app);
        }
        new AlertDialog.Builder(this)
                .setTitle(R.string.batch_add_apps)
                .setMultiChoiceItems(labels, checked, (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton(R.string.batch_apply, (dialog, which) -> {
                    Set<String> activeKeys = new HashSet<>();
                    for (AppEntry app : desktopApps) {
                        activeKeys.add(app.key);
                    }
                    for (int i = 0; i < candidates.size(); i++) {
                        if (checked[i]) {
                            activeKeys.add(candidates.get(i).key);
                        } else {
                            activeKeys.remove(candidates.get(i).key);
                        }
                    }
                    List<AppEntry> nextDesktopApps = new ArrayList<>();
                    Set<String> used = new HashSet<>();
                    for (AppEntry app : desktopApps) {
                        if (activeKeys.contains(app.key) && used.add(app.key)) {
                            nextDesktopApps.add(app);
                        }
                    }
                    for (AppEntry app : candidates) {
                        if (activeKeys.contains(app.key) && used.add(app.key)) {
                            nextDesktopApps.add(app);
                        }
                    }
                    desktopApps.clear();
                    desktopApps.addAll(nextDesktopApps);
                    sortMode = SORT_CUSTOM;
                    preferences.edit().putInt(PREF_SORT_MODE, sortMode).apply();
                    persistDesktopSelectionAndRefresh();
                    Toast.makeText(this, R.string.batch_add_done_toast, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void sortSearchResults(String normalizedQuery) {
        Collator collator = Collator.getInstance(Locale.getDefault());
        Collections.sort(filteredApps, (left, right) -> {
            int leftScore = getSearchScore(left, normalizedQuery);
            int rightScore = getSearchScore(right, normalizedQuery);
            if (leftScore != rightScore) {
                return Integer.compare(leftScore, rightScore);
            }
            int labelCompare = collator.compare(left.label, right.label);
            if (labelCompare != 0) {
                return labelCompare;
            }
            return left.packageName.compareTo(right.packageName);
        });
    }

    private int getSearchScore(AppEntry app, String normalizedQuery) {
        String label = app.label.toLowerCase(Locale.getDefault());
        String packageName = app.packageName.toLowerCase(Locale.US);
        String initialKey = getAppInitialSearchKey(app).toLowerCase(Locale.US);
        if (label.equals(normalizedQuery)) {
            return 0;
        }
        if (label.startsWith(normalizedQuery)) {
            return 1;
        }
        if (initialKey.equals(normalizedQuery) || initialKey.startsWith(normalizedQuery)) {
            return 2;
        }
        if (label.contains(normalizedQuery)) {
            return 3;
        }
        if (initialKey.contains(normalizedQuery)) {
            return 4;
        }
        if (packageName.startsWith(normalizedQuery)) {
            return 5;
        }
        return 6;
    }

    private void updateDesktopQuickLaunch() {
        if (desktopQuickLaunchPanel == null || desktopQuickLaunchRow == null) {
            return;
        }
        desktopQuickLaunchRow.removeAllViews();
        desktopQuickLaunchPanel.setVisibility(View.VISIBLE);
        addDefaultDockButton(R.string.phone_short, DOCK_PHONE, v -> openDialer());
        addDefaultDockButton(R.string.system_settings_short, DOCK_SETTINGS, v -> openSystemSettings());
        addDefaultDockButton(R.string.messages_short, DOCK_MESSAGES, v -> openMessages());
    }

    private void addDefaultDockButton(int labelRes, int iconType, View.OnClickListener listener) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.MATCH_PARENT,
                1f
        );
        params.setMargins(dp(2), 0, dp(2), 0);
        desktopQuickLaunchRow.addView(createDockButton(labelRes, iconType, listener), params);
    }

    private void updateRecentAppsPanel(boolean showWhenAvailable) {
        if (recentAppsPanel == null || recentAppsRow == null) {
            return;
        }
        recentAppsRow.removeAllViews();
        if (!showWhenAvailable || allApps.isEmpty()) {
            recentAppsPanel.setVisibility(View.GONE);
            return;
        }

        List<AppEntry> recentApps = getRecentApps(8);
        if (recentApps.isEmpty()) {
            recentAppsPanel.setVisibility(View.GONE);
            return;
        }

        recentAppsPanel.setVisibility(View.VISIBLE);
        for (AppEntry app : recentApps) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(76), ViewGroup.LayoutParams.MATCH_PARENT);
            params.setMargins(0, 0, dp(8), 0);
            recentAppsRow.addView(createRecentAppButton(app), params);
        }
    }

    private void updateSearchHistoryPanel(boolean showWhenAvailable) {
        if (searchHistoryPanel == null || searchHistoryRow == null) {
            return;
        }
        searchHistoryRow.removeAllViews();
        if (!showWhenAvailable) {
            searchHistoryPanel.setVisibility(View.GONE);
            return;
        }

        List<String> history = getSearchHistory(8);
        if (history.isEmpty()) {
            searchHistoryPanel.setVisibility(View.GONE);
            return;
        }

        searchHistoryPanel.setVisibility(View.VISIBLE);
        for (String query : history) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp(34));
            params.setMargins(0, 0, dp(8), 0);
            searchHistoryRow.addView(createSearchHistoryButton(query), params);
        }
    }

    private List<AppEntry> getRecentApps(int limit) {
        String saved = preferences.getString(PREF_RECENT_APPS, "");
        List<AppEntry> result = new ArrayList<>();
        if (TextUtils.isEmpty(saved)) {
            return result;
        }
        Set<String> used = new HashSet<>();
        String[] keys = saved.split("\\n");
        for (String key : keys) {
            if (TextUtils.isEmpty(key) || !used.add(key)) {
                continue;
            }
            AppEntry app = findAppByKey(key);
            if (app != null) {
                result.add(app);
            }
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    private List<AppEntry> getQuickLaunchApps() {
        List<AppEntry> result = new ArrayList<>();
        Set<String> used = new HashSet<>();
        for (String key : getPinnedQuickLaunchKeys()) {
            AppEntry app = findAppByKey(key);
            if (app != null && used.add(app.key)) {
                result.add(app);
            }
            if (result.size() >= 4) {
                return result;
            }
        }
        for (AppEntry app : getRecentApps(8)) {
            if (used.add(app.key)) {
                result.add(app);
            }
            if (result.size() >= 4) {
                break;
            }
        }
        return result;
    }

    private List<String> getPinnedQuickLaunchKeys() {
        return parsePreferenceKeys(PREF_PINNED_QUICK_LAUNCH, 4);
    }

    private boolean isQuickLaunchPinned(AppEntry app) {
        return app != null && pinnedQuickLaunchKeySet.contains(app.key);
    }

    private String getQuickLaunchPinnedSummary() {
        int count = getPinnedQuickLaunchApps().size();
        return count == 0
                ? getString(R.string.quick_launch_empty)
                : getString(R.string.quick_launch_count_format, count);
    }

    private void showQuickLaunchPinnedChooser() {
        if (getPinnedQuickLaunchApps().isEmpty() && !getPinnedQuickLaunchKeys().isEmpty()) {
            clearPinnedQuickLaunch();
        }
        navigateSettingsPage(SETTINGS_PAGE_QUICK_LAUNCH, SETTINGS_PAGE_MANAGEMENT);
        refreshSettingsView();
    }

    private List<AppEntry> getPinnedQuickLaunchApps() {
        List<AppEntry> apps = new ArrayList<>();
        for (String key : getPinnedQuickLaunchKeys()) {
            AppEntry app = findAppByKey(key);
            if (app != null) {
                apps.add(app);
            }
        }
        return apps;
    }

    private void setQuickLaunchPinned(AppEntry app, boolean pinned) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return;
        }
        List<String> keys = getPinnedQuickLaunchKeys();
        keys.remove(app.key);
        if (pinned) {
            keys.add(0, app.key);
        }
        while (keys.size() > 4) {
            keys.remove(keys.size() - 1);
        }
        savePinnedQuickLaunchKeys(keys);
    }

    private void savePinnedQuickLaunchKeys(List<String> keys) {
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(key);
        }
        preferences.edit().putString(PREF_PINNED_QUICK_LAUNCH, builder.toString()).apply();
        refreshPreferenceKeyCaches();
        updateDesktopQuickLaunch();
        refreshSettingsIfVisible();
    }

    private void clearPinnedQuickLaunch() {
        preferences.edit().remove(PREF_PINNED_QUICK_LAUNCH).apply();
        refreshPreferenceKeyCaches();
        updateDesktopQuickLaunch();
        refreshSettingsIfVisible();
        Toast.makeText(this, R.string.clear_pinned_quick_launch_toast, Toast.LENGTH_SHORT).show();
    }

    private void refreshSettingsIfVisible() {
        if (settingsView != null && settingsView.getVisibility() == View.VISIBLE) {
            refreshSettingsView();
        }
    }

    private AppEntry findAppByKey(String key) {
        Set<String> hiddenKeys = new HashSet<>(hiddenAppKeySet);
        for (AppEntry app : allApps) {
            if (app.key.equals(key) && !isAppHidden(app, hiddenKeys)) {
                return app;
            }
        }
        if (TextUtils.isEmpty(key) || hiddenKeys.contains(key)) {
            return null;
        }
        return resolveAppEntryByKey(key);
    }

    private List<String> getSearchHistory(int limit) {
        String saved = preferences.getString(PREF_SEARCH_HISTORY, "");
        List<String> result = new ArrayList<>();
        if (TextUtils.isEmpty(saved)) {
            return result;
        }
        Set<String> used = new HashSet<>();
        for (String query : saved.split("\\n")) {
            String normalized = query == null ? "" : query.trim();
            if (TextUtils.isEmpty(normalized)) {
                continue;
            }
            String key = normalized.toLowerCase(Locale.getDefault());
            if (!used.add(key)) {
                continue;
            }
            result.add(normalized);
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    private void recordSearchQueryFromCurrentInput() {
        if (searchView == null || searchView.getVisibility() != View.VISIBLE || searchBox == null) {
            return;
        }
        String query = searchBox.getText().toString().trim();
        if (query.length() < 2) {
            return;
        }

        List<String> queries = new ArrayList<>();
        queries.add(query);
        String queryKey = query.toLowerCase(Locale.getDefault());
        for (String savedQuery : getSearchHistory(8)) {
            if (!queryKey.equals(savedQuery.toLowerCase(Locale.getDefault())) && !queries.contains(savedQuery)) {
                queries.add(savedQuery);
            }
            if (queries.size() >= 8) {
                break;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (String item : queries) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(item);
        }
        preferences.edit().putString(PREF_SEARCH_HISTORY, builder.toString()).apply();
        updateSearchHistoryPanel(TextUtils.isEmpty(searchBox.getText().toString().trim()));
    }

    private void recordRecentApp(AppEntry app) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return;
        }
        List<String> keys = new ArrayList<>();
        keys.add(app.key);
        String saved = preferences.getString(PREF_RECENT_APPS, "");
        if (!TextUtils.isEmpty(saved)) {
            for (String key : saved.split("\\n")) {
                if (!TextUtils.isEmpty(key) && !app.key.equals(key) && !keys.contains(key)) {
                    keys.add(key);
                }
                if (keys.size() >= 8) {
                    break;
                }
            }
        }
        StringBuilder builder = new StringBuilder();
        for (String key : keys) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(key);
        }
        preferences.edit().putString(PREF_RECENT_APPS, builder.toString()).apply();
        if (searchBox != null) {
            updateRecentAppsPanel(TextUtils.isEmpty(searchBox.getText().toString().trim()));
        }
        updateDesktopQuickLaunch();
    }

    private void clearRecentApps() {
        preferences.edit().remove(PREF_RECENT_APPS).apply();
        if (searchBox != null) {
            updateRecentAppsPanel(TextUtils.isEmpty(searchBox.getText().toString().trim()));
        }
        updateDesktopQuickLaunch();
    }

    private void clearSearchHistory() {
        preferences.edit().remove(PREF_SEARCH_HISTORY).apply();
        updateSearchHistoryPanel(true);
    }

    private void clearUsageRecords() {
        preferences.edit()
                .remove(PREF_RECENT_APPS)
                .remove(PREF_SEARCH_HISTORY)
                .apply();
        if (searchBox != null) {
            boolean showPanels = TextUtils.isEmpty(searchBox.getText().toString().trim());
            updateRecentAppsPanel(showPanels);
            updateSearchHistoryPanel(showPanels);
        }
        updateDesktopQuickLaunch();
        refreshSettingsIfVisible();
        Toast.makeText(this, R.string.usage_records_cleared_toast, Toast.LENGTH_SHORT).show();
    }

    private void hideApp(AppEntry app) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return;
        }
        setQuickLaunchPinned(app, false);
        Set<String> hiddenKeys = getHiddenAppKeys();
        hiddenKeys.add(app.key);
        saveHiddenAppKeys(hiddenKeys);
        selectedEditPosition = -1;
        refreshAppsForCurrentSurface();
        Toast.makeText(this, R.string.hidden_app_toast, Toast.LENGTH_SHORT).show();
    }

    private void restoreHiddenApps() {
        if (getHiddenAppKeys().isEmpty()) {
            Toast.makeText(this, R.string.hidden_apps_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        preferences.edit().remove(PREF_HIDDEN_APPS).apply();
        refreshPreferenceKeyCaches();
        refreshAfterHiddenAppsChanged(R.string.restore_hidden_apps_toast);
    }

    private void restoreHiddenApp(AppEntry app) {
        if (app == null) {
            return;
        }
        Set<String> hiddenKeys = getHiddenAppKeys();
        if (!hiddenKeys.remove(app.key)) {
            return;
        }
        saveHiddenAppKeys(hiddenKeys);
        refreshAfterHiddenAppsChanged(R.string.restore_hidden_app_toast);
    }

    private void refreshAfterHiddenAppsChanged(int toastRes) {
        loadApps();
        Toast.makeText(this, toastRes, Toast.LENGTH_SHORT).show();
        refreshSettingsView();
    }

    private void saveHiddenAppKeys(Set<String> hiddenKeys) {
        StringBuilder builder = new StringBuilder();
        for (String key : hiddenKeys) {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(key);
        }
        preferences.edit().putString(PREF_HIDDEN_APPS, builder.toString()).apply();
        refreshPreferenceKeyCaches();
    }

    private void updateDesktopStatus() {
        if (desktopStatus == null) {
            return;
        }
        if (desktopApps.isEmpty()) {
            desktopStatus.setText(R.string.desktop_empty_hint);
            updateDesktopHint();
        } else if (editMode) {
            int statusRes = selectedEditPosition >= 0
                    ? R.string.desktop_edit_target_status_format
                    : R.string.desktop_edit_status_format;
            desktopStatus.setText(getString(statusRes,
                    currentDesktopPage + 1,
                    getDesktopPageCount()));
            updateDesktopHint();
        } else {
            int statusRes = desktopMode == 12
                    ? R.string.desktop_status_twelve_format
                    : R.string.desktop_status_twenty_format;
            desktopStatus.setText(getString(statusRes,
                    currentDesktopPage + 1,
                    getDesktopPageCount(),
                    desktopApps.size()));
            updateDesktopHint();
        }
    }

    private void updateDesktopHint() {
        if (desktopHint == null) {
            return;
        }
        if (editMode) {
            desktopHint.setText(R.string.edit_mode_hint);
        } else if (fastLaunchEnabled) {
            desktopHint.setText(R.string.fast_launch_desktop_hint);
        } else {
            desktopHint.setText(R.string.swipe_search_hint);
        }
        desktopHint.setTextColor(editMode ? Color.rgb(218, 150, 142) : Color.rgb(132, 139, 145));
    }

    private void updatePageIndicator() {
        if (pageIndicator == null) {
            return;
        }
        pageIndicator.removeAllViews();
        int pageCount = getDesktopPageCount();
        if (pageCount <= 1) {
            pageIndicator.setVisibility(View.GONE);
            return;
        }
        pageIndicator.setVisibility(View.VISIBLE);
        int availableWidth = Math.max(dp(120), getResources().getDisplayMetrics().widthPixels - dp(18));
        int hitWidth = Math.max(dp(9), Math.min(dp(30), availableWidth / pageCount));
        int inactiveSize = Math.max(dp(4), Math.min(dp(7), hitWidth - dp(5)));
        int activeWidth = Math.max(dp(7), Math.min(dp(12), hitWidth - dp(3)));
        int activeHeight = Math.max(dp(6), Math.min(dp(8), hitWidth - dp(5)));
        for (int i = 0; i < pageCount; i++) {
            final int targetPage = i;
            FrameLayout dotHitArea = new FrameLayout(this);
            dotHitArea.setClickable(true);
            dotHitArea.setFocusable(true);
            dotHitArea.setContentDescription(getString(R.string.desktop_page_indicator_format, i + 1, pageCount));
            dotHitArea.setOnClickListener(v -> handlePageIndicatorTap(targetPage));
            dotHitArea.setOnDragListener((view, event) -> handlePageIndicatorDragEvent(event, targetPage));

            View dot = new View(this);
            boolean active = i == currentDesktopPage;
            int dotWidth = active ? activeWidth : inactiveSize;
            int dotHeight = active ? activeHeight : inactiveSize;
            dot.setBackground(new DesktopPageIndicatorDotDrawable(active, editMode));
            dotHitArea.addView(dot, new FrameLayout.LayoutParams(dotWidth, dotHeight, Gravity.CENTER));
            pageIndicator.addView(dotHitArea, new LinearLayout.LayoutParams(hitWidth, ViewGroup.LayoutParams.MATCH_PARENT));
            if (active) {
                dot.setScaleX(0.82f);
                dot.setScaleY(0.82f);
                dot.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(160L)
                        .setInterpolator(new DecelerateInterpolator(1.35f))
                        .start();
            }
        }
    }

    private void updateSearchStatus() {
        if (searchStatus == null) {
            return;
        }
        if (!appsLoaded) {
            searchStatus.setText(R.string.loading_apps);
        } else if (allApps.isEmpty()) {
            searchStatus.setText(R.string.empty_apps);
        } else if (filteredApps.isEmpty()) {
            searchStatus.setText(R.string.no_results);
        } else {
            searchStatus.setText(getString(R.string.apps_count_format, filteredApps.size()));
        }
    }

    private void launchFirstSearchResult() {
        String query = searchBox == null ? "" : searchBox.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, R.string.search_empty_query, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isDialQuery(query)) {
            openDialer(query);
            return;
        }
        if (filteredApps.isEmpty()) {
            Toast.makeText(this, R.string.no_results, Toast.LENGTH_SHORT).show();
            return;
        }
        openApp(filteredApps.get(0));
    }

    private void dialSearchNumber() {
        String query = searchBox == null ? "" : searchBox.getText().toString().trim();
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, R.string.dial_empty_number, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDialQuery(query)) {
            Toast.makeText(this, R.string.dial_invalid_number, Toast.LENGTH_SHORT).show();
            return;
        }
        openDialer(query);
    }

    private boolean isDialQuery(String query) {
        if (TextUtils.isEmpty(query)) {
            return false;
        }
        boolean hasDigit = false;
        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);
            if (c >= '0' && c <= '9') {
                hasDigit = true;
                continue;
            }
            if (c == '+' || c == '*' || c == '#' || c == '-' || c == ' ' || c == '(' || c == ')') {
                continue;
            }
            return false;
        }
        return hasDigit;
    }

    private void openApp(AppEntry app) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(app.componentName);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            startActivity(intent);
            recordRecentApp(app);
            recordSearchQueryFromCurrentInput();
        } catch (ActivityNotFoundException | SecurityException firstFailure) {
            Intent fallback = getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (fallback == null) {
                Toast.makeText(this, R.string.open_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            try {
                startActivity(fallback);
                recordRecentApp(app);
                recordSearchQueryFromCurrentInput();
            } catch (ActivityNotFoundException | SecurityException secondFailure) {
                Toast.makeText(this, R.string.open_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openAppDetails(AppEntry app) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + app.packageName));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException exception) {
            Toast.makeText(this, R.string.open_details_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAppActions(AppEntry app) {
        boolean pinned = isQuickLaunchPinned(app);
        boolean onDesktop = isAppOnDesktop(app);
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(createAppActionsPanel(app, dialog, pinned, onDesktop));
        dialog.setOnShowListener(shown -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
                int width = Math.min(getResources().getDisplayMetrics().widthPixels - dp(40), dp(430));
                window.setLayout(Math.max(dp(300), width), ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        });
        dialog.show();
    }

    private View createAppActionsPanel(AppEntry app, AlertDialog dialog, boolean pinned, boolean onDesktop) {
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setPadding(dp(16), dp(14), dp(16), dp(12));
        panel.setBackground(new ActionSheetBackgroundDrawable());

        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(4), dp(4), dp(4), dp(12));
        panel.addView(header, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView icon = new ImageView(this);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        icon.setImageDrawable(createDisplayIcon(app));
        header.addView(icon, new LinearLayout.LayoutParams(dp(48), dp(48)));

        LinearLayout titles = new LinearLayout(this);
        titles.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.setMargins(dp(14), 0, 0, 0);
        header.addView(titles, titleParams);

        TextView title = new TextView(this);
        title.setText(app.label);
        title.setTextColor(Color.rgb(241, 244, 245));
        title.setTextSize(20);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setSingleLine(true);
        title.setEllipsize(TextUtils.TruncateAt.END);
        titles.addView(title);

        TextView packageName = new TextView(this);
        packageName.setText(app.packageName);
        packageName.setTextColor(Color.rgb(150, 158, 164));
        packageName.setTextSize(12);
        packageName.setSingleLine(true);
        packageName.setEllipsize(TextUtils.TruncateAt.END);
        titles.addView(packageName);

        addActionSheetRow(panel, ACTION_HOME,
                onDesktop ? R.string.edit_action_remove_desktop : R.string.edit_action_add_desktop,
                onDesktop ? R.string.action_home_remove_summary : R.string.action_home_add_summary,
                false,
                () -> {
                    dialog.dismiss();
                    if (onDesktop) {
                        removeAppFromDesktop(app, true);
                    } else {
                        addAppToDesktop(app, true);
                    }
                });
        addActionSheetRow(panel, ACTION_DOCK,
                pinned ? R.string.edit_action_unpin_quick_launch : R.string.edit_action_pin_quick_launch,
                pinned ? R.string.action_dock_unpin_summary : R.string.action_dock_pin_summary,
                false,
                () -> {
                    dialog.dismiss();
                    setQuickLaunchPinned(app, !pinned);
                    Toast.makeText(this, pinned ? R.string.desktop_app_unpinned_dock_toast : R.string.desktop_app_pinned_dock_toast,
                            Toast.LENGTH_SHORT).show();
                });
        addActionSheetRow(panel, ACTION_INFO, R.string.edit_action_app_info, R.string.action_info_summary, false,
                () -> {
                    dialog.dismiss();
                    openAppDetails(app);
                });
        addActionSheetRow(panel, ACTION_HIDE, R.string.edit_action_hide_app, R.string.action_hide_summary, false,
                () -> {
                    dialog.dismiss();
                    confirmHideApp(app);
                });
        addActionSheetRow(panel, ACTION_UNINSTALL, R.string.edit_action_uninstall, R.string.action_uninstall_summary, true,
                () -> {
                    dialog.dismiss();
                    confirmUninstallApp(app);
                });
        return panel;
    }

    private void addActionSheetRow(LinearLayout parent, int iconType, int titleRes, int summaryRes,
                                   boolean destructive, Runnable action) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(10), 0, dp(10), 0);
        row.setMinimumHeight(dp(62));
        row.setClickable(true);
        row.setFocusable(true);
        row.setBackground(new ActionSheetRowDrawable(destructive));
        row.setOnClickListener(v -> action.run());

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(new ActionIconDrawable(iconType, destructive));
        row.addView(icon, new LinearLayout.LayoutParams(dp(34), dp(34)));

        LinearLayout texts = new LinearLayout(this);
        texts.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textParams.setMargins(dp(12), 0, 0, 0);
        row.addView(texts, textParams);

        TextView title = new TextView(this);
        title.setText(titleRes);
        title.setTextSize(16);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(destructive ? Color.rgb(236, 128, 116) : Color.rgb(230, 234, 236));
        title.setSingleLine(true);
        texts.addView(title);

        TextView summary = new TextView(this);
        summary.setText(summaryRes);
        summary.setTextSize(11);
        summary.setTextColor(Color.rgb(145, 153, 160));
        summary.setSingleLine(true);
        summary.setEllipsize(TextUtils.TruncateAt.END);
        texts.addView(summary);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(62)
        );
        params.setMargins(0, dp(5), 0, 0);
        parent.addView(row, params);
    }

    private void confirmHideApp(AppEntry app) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_hide_app_title)
                .setMessage(getString(R.string.confirm_hide_app_message, app.label))
                .setPositiveButton(R.string.confirm_hide_app_positive, (dialog, which) -> hideApp(app))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void confirmUninstallApp(AppEntry app) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_uninstall_app_title)
                .setMessage(getString(R.string.confirm_uninstall_app_message, app.label))
                .setPositiveButton(R.string.confirm_uninstall_app_positive, (dialog, which) -> openUninstallApp(app))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openUninstallApp(AppEntry app) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + app.packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException exception) {
            Toast.makeText(this, R.string.open_uninstall_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void openSystemSettings() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException firstFailure) {
            try {
                startActivity(new Intent("android.settings.SETTINGS").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            } catch (ActivityNotFoundException | SecurityException secondFailure) {
                Toast.makeText(this, R.string.open_settings_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openDialer() {
        openDialer("");
    }

    private void openDialer(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        if (!TextUtils.isEmpty(phoneNumber)) {
            intent.setData(Uri.fromParts("tel", phoneNumber, null));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException exception) {
            Toast.makeText(this, R.string.open_phone_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void openMessages() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException | SecurityException firstFailure) {
            Intent fallback = new Intent(Intent.ACTION_VIEW);
            fallback.setData(Uri.parse("sms:"));
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                startActivity(fallback);
            } catch (ActivityNotFoundException | SecurityException secondFailure) {
                Toast.makeText(this, R.string.open_messages_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void applyDesktopTheme() {
        preloadCurrentThemeTextures();
        if (root != null) {
            root.setBackground(createDesktopBackgroundDrawable());
        }
        if (desktopPage != null) {
            desktopPage.setBackground(createDesktopBackgroundDrawable());
        }
        if (desktopTopBar != null) {
            desktopTopBar.setBackgroundColor(getDesktopTopBarColor());
        }
        if (desktopGrid != null) {
            desktopGrid.setBackgroundColor(Color.TRANSPARENT);
        }
        if (desktopGridLayer != null) {
            desktopGridLayer.setBackgroundColor(Color.TRANSPARENT);
        }
        updateDesktopGridInsets();
        if (pageIndicator != null) {
            pageIndicator.setBackground(createDesktopBackgroundDrawable());
        }
        if (desktopView != null && desktopView.getVisibility() == View.VISIBLE
                && (searchView == null || searchView.getVisibility() != View.VISIBLE)
                && (settingsView == null || settingsView.getVisibility() != View.VISIBLE)) {
            configureSystemBars(false);
        }
        updateDockBackground();
        if (desktopAdapter != null) {
            desktopAdapter.notifyDataSetChanged();
        }
        updateDesktopQuickLaunch();
    }

    private Drawable createDesktopBackgroundDrawable() {
        if (transparentThemeEnabled) {
            return new FrostedWallpaperDrawable(getFrostedWallpaperBitmap(),
                    getDesktopBackgroundTopColor(), getDesktopBackgroundBottomColor());
        }
        return new DesktopBackdropDrawable(getDesktopBackgroundTopColor(), getDesktopBackgroundBottomColor());
    }

    private int getDesktopBackgroundTopColor() {
        if (transparentThemeEnabled) {
            return Color.rgb(34, 39, 43);
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return getSmartisanTextureColor(smartisanTheme, 0, TEXTURE_SAMPLE_TOP,
                    getSmartisanFallbackPalette(smartisanTheme).topColor);
        }
        if (isPantoneThemeValue(desktopTheme)) {
            return getThemePreviewPalette(desktopTheme).topColor;
        }
        switch (desktopTheme) {
            case THEME_GRAPHITE:
                return Color.rgb(33, 38, 42);
            case THEME_COPPER:
                return Color.rgb(56, 43, 40);
            case THEME_ORIGINAL_BLUE:
            case THEME_CLASSIC_BLUE_TEXTURE:
                return Color.rgb(28, 45, 62);
            case THEME_CLASSIC:
            default:
                return Color.rgb(44, 49, 53);
        }
    }

    private int getDesktopBackgroundBottomColor() {
        if (transparentThemeEnabled) {
            return Color.rgb(21, 25, 29);
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return getSmartisanTextureColor(smartisanTheme, 0, TEXTURE_SAMPLE_BOTTOM,
                    getSmartisanFallbackPalette(smartisanTheme).bottomColor);
        }
        if (isPantoneThemeValue(desktopTheme)) {
            return getThemePreviewPalette(desktopTheme).bottomColor;
        }
        switch (desktopTheme) {
            case THEME_GRAPHITE:
                return Color.rgb(24, 29, 33);
            case THEME_COPPER:
                return Color.rgb(38, 34, 34);
            case THEME_ORIGINAL_BLUE:
            case THEME_CLASSIC_BLUE_TEXTURE:
                return Color.rgb(20, 32, 45);
            case THEME_CLASSIC:
            default:
                return Color.rgb(35, 40, 44);
        }
    }

    private int getDesktopTopBarColor() {
        if (transparentThemeEnabled) {
            return Color.rgb(28, 32, 36);
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return getSmartisanSystemBarColor(smartisanTheme, true);
        }
        if (isPantoneThemeValue(desktopTheme)) {
            return mixColor(getThemePreviewPalette(desktopTheme).topColor, Color.BLACK, 0.20f);
        }
        switch (desktopTheme) {
            case THEME_GRAPHITE:
                return Color.rgb(29, 34, 38);
            case THEME_COPPER:
                return Color.rgb(49, 39, 37);
            case THEME_ORIGINAL_BLUE:
            case THEME_CLASSIC_BLUE_TEXTURE:
                return Color.rgb(23, 38, 54);
            case THEME_CLASSIC:
            default:
                return Color.rgb(37, 42, 46);
        }
    }

    private int getDesktopNavigationBarColor() {
        if (transparentThemeEnabled) {
            return Color.rgb(18, 21, 24);
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return getSmartisanSystemBarColor(smartisanTheme, false);
        }
        if (isPantoneThemeValue(desktopTheme)) {
            return mixColor(getThemePreviewPalette(desktopTheme).bottomColor, Color.BLACK, 0.34f);
        }
        switch (desktopTheme) {
            case THEME_GRAPHITE:
                return Color.rgb(22, 27, 31);
            case THEME_COPPER:
                return Color.rgb(33, 29, 29);
            case THEME_ORIGINAL_BLUE:
            case THEME_CLASSIC_BLUE_TEXTURE:
                return Color.rgb(16, 27, 39);
            case THEME_CLASSIC:
            default:
                return Color.rgb(31, 36, 40);
        }
    }

    private int getSmartisanSystemBarColor(SmartisanTheme smartisanTheme, boolean statusBar) {
        ThemePalette fallback = getSmartisanFallbackPalette(smartisanTheme);
        if (statusBar) {
            int color = getSmartisanTextureColor(smartisanTheme, 0, TEXTURE_SAMPLE_TOP, fallback.topColor);
            return mixColor(color, Color.BLACK, isLightColor(color) ? 0.16f : 0.08f);
        }
        int color = getSmartisanDockTextureColor(smartisanTheme, fallback.dockColor);
        return mixColor(color, Color.BLACK, isLightColor(color) ? 0.18f : 0.08f);
    }

    private Drawable createDockBackgroundDrawable() {
        if (transparentThemeEnabled) {
            return new TransparentDockDrawable();
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return new ThemeTextureDrawable(getResources(), getSmartisanThemeTexturePath(smartisanTheme, -1), false);
        }
        if (desktopTheme == THEME_CLASSIC) {
            return new ThemeTextureDrawable(getResources(), getClassicDockTexturePath(), false);
        }
        return new DesktopDockDrawable(desktopTheme);
    }

    private void configureSystemBars(boolean light) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            int barColor = light ? Color.rgb(247, 247, 245) : getDesktopTopBarColor();
            window.setStatusBarColor(barColor);
            window.setNavigationBarColor(light ? Color.rgb(247, 247, 245) : getDesktopNavigationBarColor());
            window.getDecorView().setSystemUiVisibility(light ? View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR : 0);
        }
    }

    private GradientDrawable rounded(int fillColor, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(fillColor);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    private static int mixColor(int from, int to, float amount) {
        float clamped = Math.max(0f, Math.min(1f, amount));
        int a = Math.round(Color.alpha(from) + (Color.alpha(to) - Color.alpha(from)) * clamped);
        int r = Math.round(Color.red(from) + (Color.red(to) - Color.red(from)) * clamped);
        int g = Math.round(Color.green(from) + (Color.green(to) - Color.green(from)) * clamped);
        int b = Math.round(Color.blue(from) + (Color.blue(to) - Color.blue(from)) * clamped);
        return Color.argb(a, r, g, b);
    }

    private static boolean isLightColor(int color) {
        return (Color.red(color) * 299 + Color.green(color) * 587 + Color.blue(color) * 114) >= 162000;
    }

    private Drawable createDesktopCellBackground(int position, boolean selected) {
        if (transparentThemeEnabled) {
            return new TransparentCellDrawable(position, selected, desktopMode);
        }
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            return new ThemeTextureDrawable(getResources(), getSmartisanThemeTexturePath(smartisanTheme, position), selected);
        }
        if (desktopTheme == THEME_CLASSIC) {
            return new ClassicCellTextureDrawable(getResources(), getClassicCellTexturePath(false),
                    getClassicCellTexturePath(true), selected);
        }
        return new DesktopCellDrawable(desktopTheme, selected);
    }

    private void preloadCurrentThemeTextures() {
        final List<String> paths = getCurrentThemeTexturePaths();
        if (paths.isEmpty()) {
            return;
        }
        final Resources resources = getResources();
        final int token = ++texturePreloadToken;
        textureExecutor.execute(() -> {
            for (String path : paths) {
                if (activityDestroyed || token != texturePreloadToken) {
                    return;
                }
                ThemeTextureDrawable.preloadBitmap(resources, path);
            }
        });
    }

    private List<String> getCurrentThemeTexturePaths() {
        List<String> paths = new ArrayList<>();
        SmartisanTheme smartisanTheme = getSmartisanTheme(desktopTheme);
        if (smartisanTheme != null) {
            int count = desktopMode == 20 ? 20 : 12;
            paths.add(getSmartisanThemeTexturePath(smartisanTheme, -1));
            for (int i = 0; i < count; i++) {
                paths.add(getSmartisanThemeTexturePath(smartisanTheme, i));
            }
            return paths;
        }
        if (desktopTheme == THEME_CLASSIC) {
            paths.add(getClassicDockTexturePath());
            paths.add(getClassicCellTexturePath(false));
            paths.add(getClassicCellTexturePath(true));
        }
        return paths;
    }

    private String getClassicDockTexturePath() {
        int mode = desktopMode == 20 ? 20 : 12;
        return "smartisan/textures/mode" + mode + "/dock_back.png";
    }

    private String getClassicCellTexturePath(boolean selected) {
        int mode = desktopMode == 20 ? 20 : 12;
        return "smartisan/textures/mode" + mode + "/" + (selected ? "brick_selected.png" : "brick_unselect.png");
    }

    private String getSmartisanThemeTexturePath(SmartisanTheme theme, int position) {
        int mode = desktopMode == 20 ? 20 : 12;
        if (position < 0) {
            return "smartisan_themes/" + theme.assetDir + "/" + mode + "/dock_back.webp";
        }
        int max = mode == 20 ? 20 : 12;
        int index = Math.min(Math.max(0, position), max - 1) + 1;
        return "smartisan_themes/" + theme.assetDir + "/" + mode + "/back" + index + ".webp";
    }

    private Drawable createDisplayIcon(AppEntry app) {
        Drawable icon = app == null ? null : app.icon;
        if (icon == null) {
            requestAppIcon(app);
        }
        icon = cloneDrawable(icon);
        if (iconStyle == ICON_STYLE_ORIGINAL) {
            return icon;
        }
        return new AppIconDrawable(icon, getIconPlateColor(), getIconPlateStrokeColor());
    }

    private void requestAppIcon(AppEntry app) {
        if (app == null || app.icon != null || app.iconLoadQueued || app.iconLoadFailed) {
            return;
        }
        app.iconLoadQueued = true;
        iconExecutor.execute(() -> {
            if (activityDestroyed) {
                return;
            }
            Drawable loadedIcon = loadAppIcon(app);
            mainHandler.post(() -> {
                if (activityDestroyed) {
                    return;
                }
                app.icon = loadedIcon;
                app.iconLoadFailed = loadedIcon == null;
                app.iconLoadQueued = false;
                scheduleIconRefresh(app);
            });
        });
    }

    private void scheduleIconRefresh(AppEntry app) {
        if (app == null || TextUtils.isEmpty(app.key)) {
            return;
        }
        boolean needsDesktopRefresh = desktopAppKeySet.contains(app.key)
                && desktopView != null
                && desktopView.getVisibility() == View.VISIBLE;
        boolean needsSearchRefresh = searchView != null
                && searchView.getVisibility() == View.VISIBLE
                && containsAppKey(filteredApps, app.key);
        if (!needsDesktopRefresh && !needsSearchRefresh) {
            return;
        }
        pendingDesktopIconRefresh |= needsDesktopRefresh;
        pendingSearchIconRefresh |= needsSearchRefresh;
        mainHandler.removeCallbacks(iconRefreshRunnable);
        mainHandler.postDelayed(iconRefreshRunnable, 32L);
    }

    private boolean containsAppKey(List<AppEntry> apps, String key) {
        if (apps == null || TextUtils.isEmpty(key)) {
            return false;
        }
        for (AppEntry app : apps) {
            if (app != null && key.equals(app.key)) {
                return true;
            }
        }
        return false;
    }

    private Drawable cloneDrawable(Drawable drawable) {
        if (drawable == null) {
            return rounded(Color.rgb(120, 126, 132), 0, 0, 0);
        }
        Drawable.ConstantState state = drawable.getConstantState();
        Drawable copy = state == null ? drawable : state.newDrawable(getResources());
        return copy.mutate();
    }

    private int getIconPlateColor() {
        if (isPantoneThemeValue(desktopTheme)) {
            return mixColor(getThemePreviewPalette(desktopTheme).cellColor, Color.WHITE, 0.58f);
        }
        switch (desktopTheme) {
            case THEME_COPPER:
                return Color.rgb(224, 218, 211);
            case THEME_GRAPHITE:
                return Color.rgb(220, 224, 226);
            case THEME_CLASSIC:
            default:
                return Color.rgb(226, 228, 229);
        }
    }

    private int getIconPlateStrokeColor() {
        if (isPantoneThemeValue(desktopTheme)) {
            return mixColor(getThemePreviewPalette(desktopTheme).accentColor, Color.BLACK, 0.18f);
        }
        switch (desktopTheme) {
            case THEME_COPPER:
                return Color.rgb(164, 150, 140);
            case THEME_GRAPHITE:
                return Color.rgb(150, 157, 162);
            case THEME_CLASSIC:
            default:
                return Color.rgb(158, 164, 168);
        }
    }

    private void updateDockBackground() {
        if (desktopDock == null) {
            return;
        }
        desktopDock.setBackground(createDockBackgroundDrawable());
        updateDesktopDockHeight();
    }

    private void updateDesktopGridInsets() {
        if (desktopGrid == null) {
            return;
        }
        int horizontal = transparentThemeEnabled ? dp(desktopMode == 20 ? 18 : 12) : 0;
        int top = transparentThemeEnabled ? dp(desktopMode == 20 ? 14 : 22) : 0;
        int bottom = transparentThemeEnabled ? dp(desktopMode == 20 ? 12 : 18) : 0;
        if (desktopGrid.getPaddingLeft() != horizontal
                || desktopGrid.getPaddingTop() != top
                || desktopGrid.getPaddingRight() != horizontal
                || desktopGrid.getPaddingBottom() != bottom) {
            desktopGrid.setPadding(horizontal, top, horizontal, bottom);
            desktopGrid.requestLayout();
        }
        desktopGrid.invalidateViews();
    }

    private Bitmap getFrostedWallpaperBitmap() {
        if (!transparentThemeEnabled) {
            return null;
        }
        if (frostedWallpaperBitmap != null && !frostedWallpaperBitmap.isRecycled()) {
            return frostedWallpaperBitmap;
        }
        try {
            Drawable wallpaper = WallpaperManager.getInstance(this).getDrawable();
            if (wallpaper == null) {
                return null;
            }
            int width = Math.max(48, getResources().getDisplayMetrics().widthPixels / 14);
            int height = Math.max(86, getResources().getDisplayMetrics().heightPixels / 14);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            wallpaper.setBounds(0, 0, width, height);
            wallpaper.draw(canvas);
            frostedWallpaperBitmap = bitmap;
            return frostedWallpaperBitmap;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private void recycleFrostedWallpaper() {
        if (frostedWallpaperBitmap != null && !frostedWallpaperBitmap.isRecycled()) {
            frostedWallpaperBitmap.recycle();
        }
        frostedWallpaperBitmap = null;
    }

    private void updateDesktopDockHeight() {
        if (desktopDock == null || desktopPage == null || desktopPage.getHeight() <= 0) {
            return;
        }
        int pageHeight = desktopPage.getHeight();
        int targetHeight = Math.round(pageHeight * (desktopMode == 12 ? 0.19f : 0.155f));
        if (transparentThemeEnabled) {
            targetHeight = Math.round(targetHeight * 0.94f);
        }
        int comfortableMinHeight = dp(desktopMode == 12 ? 132 : 112);
        int compactMinHeight = dp(desktopMode == 12 ? 112 : 96);
        int maxHeight = dp(desktopMode == 12 ? 184 : 166);
        targetHeight = Math.max(comfortableMinHeight, Math.min(maxHeight, targetHeight));

        int topBarHeight = desktopTopBar == null ? 0 : desktopTopBar.getHeight();
        int indicatorHeight = pageIndicator == null || pageIndicator.getVisibility() == View.GONE
                ? 0 : pageIndicator.getHeight();
        int gridMinCellHeight = dp(desktopMode == 12 ? 126 : 106);
        int gridReservedHeight = topBarHeight + indicatorHeight + getDesktopRows() * gridMinCellHeight;
        int maxDockForGrid = pageHeight - gridReservedHeight;
        if (maxDockForGrid > 0) {
            targetHeight = Math.min(targetHeight, Math.max(compactMinHeight, maxDockForGrid));
        }
        targetHeight = Math.max(compactMinHeight, targetHeight);
        ViewGroup.LayoutParams params = desktopDock.getLayoutParams();
        if (params != null && params.height != targetHeight) {
            params.height = targetHeight;
            desktopDock.setLayoutParams(params);
        }
    }

    private void addVerticalSpace(LinearLayout parent, int height) {
        View spacer = new View(this);
        parent.addView(spacer, new LinearLayout.LayoutParams(1, height));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private interface SwitchCallback {
        void onChanged(boolean checked);
    }

    private static final class SmartisanTheme {
        final String assetDir;
        final int labelResId;

        SmartisanTheme(String assetDir, int labelResId) {
            this.assetDir = assetDir;
            this.labelResId = labelResId;
        }
    }

    private static final class PantoneTheme {
        final int year;
        final String name;
        final int primaryColor;
        final int accentColor;
        final boolean light;

        PantoneTheme(int year, String name, int primaryColor, int accentColor, boolean light) {
            this.year = year;
            this.name = name;
            this.primaryColor = primaryColor;
            this.accentColor = accentColor == 0 ? primaryColor : accentColor;
            this.light = light;
        }

        String getLabel() {
            return year + " " + name;
        }
    }

    private static final class ThemePalette {
        final int topColor;
        final int bottomColor;
        final int cellColor;
        final int dockColor;
        final int accentColor;
        final boolean light;

        ThemePalette(int topColor, int bottomColor, int cellColor, int dockColor, int accentColor, boolean light) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
            this.cellColor = cellColor;
            this.dockColor = dockColor;
            this.accentColor = accentColor;
            this.light = light;
        }
    }

    private static final class AppEntry {
        final String label;
        final String packageName;
        final ComponentName componentName;
        final String key;
        Drawable icon;
        boolean iconLoadQueued;
        boolean iconLoadFailed;
        long firstInstallTime;

        AppEntry(String label, String packageName, ComponentName componentName, Drawable icon, String key, long firstInstallTime) {
            this.label = label;
            this.packageName = packageName;
            this.componentName = componentName;
            this.icon = icon;
            this.key = key;
            this.firstInstallTime = firstInstallTime;
        }
    }

    private static final class AppDragPayload {
        final AppEntry app;
        boolean handled;

        AppDragPayload(AppEntry app) {
            this.app = app;
        }
    }

    private final class DesktopAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return getDesktopPageSize();
        }

        @Override
        public AppEntry getItem(int position) {
            return getDesktopApp(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DesktopHolder holder;
            if (convertView == null) {
                LinearLayout cell = new LinearLayout(MainActivity.this);
                cell.setOrientation(LinearLayout.VERTICAL);
                cell.setGravity(Gravity.CENTER);
                cell.setPadding(dp(5), dp(10), dp(5), dp(8));
                int height = getDesktopCellHeight(parent);
                cell.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        height
                ));

                ImageView icon = new ImageView(MainActivity.this);
                icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int iconSize = getDesktopIconSize();
                cell.addView(icon, new LinearLayout.LayoutParams(iconSize, iconSize));

                TextView label = new TextView(MainActivity.this);
                label.setTextColor(Color.rgb(184, 187, 190));
                label.setTextSize(getDesktopLabelTextSize());
                label.setGravity(Gravity.CENTER);
                label.setSingleLine(true);
                label.setEllipsize(TextUtils.TruncateAt.END);
                label.setShadowLayer(2.2f, 0f, 2f, Color.argb(170, 0, 0, 0));
                LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                labelParams.setMargins(0, dp(8), 0, 0);
                cell.addView(label, labelParams);

                holder = new DesktopHolder(icon, label);
                cell.setTag(holder);
                convertView = cell;
            } else {
                holder = (DesktopHolder) convertView.getTag();
                ViewGroup.LayoutParams params = convertView.getLayoutParams();
                params.height = getDesktopCellHeight(parent);
                convertView.setLayoutParams(params);
            }

            int iconSize = getDesktopIconSize();
            ViewGroup.LayoutParams iconParams = holder.icon.getLayoutParams();
            iconParams.width = iconSize;
            iconParams.height = iconSize;
            holder.icon.setLayoutParams(iconParams);
            holder.label.setTextSize(getDesktopLabelTextSize());
            boolean brightLabel = isSmartisanTextureTheme() || transparentThemeEnabled;
            holder.label.setTextColor(brightLabel ? Color.WHITE : Color.rgb(184, 187, 190));
            holder.label.setShadowLayer(brightLabel ? 2.6f : 2.2f,
                    0f, brightLabel ? 2.4f : 2f, Color.argb(170, 0, 0, 0));
            LinearLayout.LayoutParams labelParams = (LinearLayout.LayoutParams) holder.label.getLayoutParams();
            labelParams.setMargins(0, dp(8), 0, 0);
            holder.label.setLayoutParams(labelParams);
            convertView.setPadding(dp(5), dp(10), dp(5), dp(8));

            AppEntry app = getItem(position);
            convertView.setOnDragListener((view, event) -> handleDesktopDragEvent(event, position));
            convertView.setBackground(createDesktopCellBackground(position, editMode && selectedEditPosition == position));
            if (app == null) {
                holder.icon.setImageDrawable(null);
                holder.icon.setVisibility(View.INVISIBLE);
                holder.label.setText("");
                holder.label.setVisibility(View.INVISIBLE);
            } else {
                holder.icon.setImageDrawable(createDisplayIcon(app));
                holder.icon.setVisibility(View.VISIBLE);
                holder.label.setText(app.label);
                holder.label.setVisibility(hideDesktopLabels ? View.GONE : View.VISIBLE);
            }
            applyDesktopCellTransforms(convertView, position, app != null);
            return convertView;
        }

        private int getDesktopCellHeight(ViewGroup parent) {
            int fallback = desktopMode == 12 ? dp(132) : dp(108);
            if (parent == null || parent.getHeight() <= 0) {
                return fallback;
            }
            int availableHeight = parent.getHeight() - parent.getPaddingTop() - parent.getPaddingBottom();
            if (availableHeight <= 0) {
                return fallback;
            }
            return Math.max(dp(72), Math.round(availableHeight / (float) getDesktopRows()));
        }
    }

    private final class AppAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return filteredApps.size();
        }

        @Override
        public AppEntry getItem(int position) {
            return filteredApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            RowHolder holder;
            if (convertView == null) {
                LinearLayout row = new LinearLayout(MainActivity.this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(dp(22), dp(12), dp(18), dp(12));
                row.setMinimumHeight(dp(78));
                row.setBackgroundColor(Color.WHITE);

                ImageView icon = new ImageView(MainActivity.this);
                icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
                int iconSize = getSearchIconSize();
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
                iconParams.setMargins(0, 0, dp(22), 0);
                row.addView(icon, iconParams);

                LinearLayout texts = new LinearLayout(MainActivity.this);
                texts.setOrientation(LinearLayout.VERTICAL);
                texts.setGravity(Gravity.CENTER_VERTICAL);
                row.addView(texts, new LinearLayout.LayoutParams(
                        0,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        1f
                ));

                TextView label = new TextView(MainActivity.this);
                label.setTextColor(Color.rgb(50, 54, 58));
                label.setTextSize(21);
                label.setSingleLine(true);
                label.setEllipsize(TextUtils.TruncateAt.END);
                label.setTextIsSelectable(false);
                texts.addView(label, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                TextView packageName = new TextView(MainActivity.this);
                packageName.setTextColor(Color.rgb(148, 152, 156));
                packageName.setTextSize(12);
                packageName.setSingleLine(true);
                packageName.setEllipsize(TextUtils.TruncateAt.END);
                packageName.setTextIsSelectable(false);
                texts.addView(packageName, new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                TextView marker = new TextView(MainActivity.this);
                marker.setGravity(Gravity.CENTER);
                marker.setSingleLine(true);
                marker.setTypeface(Typeface.DEFAULT_BOLD);
                marker.setClickable(true);
                marker.setFocusable(true);
                LinearLayout.LayoutParams markerParams = new LinearLayout.LayoutParams(dp(54), dp(30));
                markerParams.setMargins(dp(12), 0, 0, 0);
                row.addView(marker, markerParams);

                holder = new RowHolder(icon, label, packageName, marker);
                row.setTag(holder);
                convertView = row;
            } else {
                holder = (RowHolder) convertView.getTag();
            }

            int iconSize = getSearchIconSize();
            ViewGroup.LayoutParams iconParams = holder.icon.getLayoutParams();
            iconParams.width = iconSize;
            iconParams.height = iconSize;
            holder.icon.setLayoutParams(iconParams);

            AppEntry app = getItem(position);
            holder.icon.setImageDrawable(createDisplayIcon(app));
            holder.label.setText(app.label);
            holder.packageName.setText(app.packageName);
            boolean onDesktop = isAppOnDesktop(app);
            holder.marker.setVisibility(allAppsAddMode ? View.VISIBLE : View.GONE);
            holder.marker.setClickable(allAppsAddMode);
            holder.marker.setFocusable(allAppsAddMode);
            if (allAppsAddMode) {
                holder.marker.setText(onDesktop ? R.string.desktop_added_marker : R.string.desktop_add_marker);
                holder.marker.setTextSize(onDesktop ? 12 : 20);
                holder.marker.setTextColor(onDesktop ? Color.rgb(112, 86, 70) : Color.rgb(91, 97, 103));
                holder.marker.setBackground(onDesktop
                        ? rounded(Color.rgb(241, 230, 219), dp(15), dp(1), Color.rgb(211, 190, 170))
                        : rounded(Color.rgb(244, 245, 245), dp(15), dp(1), Color.rgb(218, 220, 222)));
                holder.marker.setOnClickListener(v -> showAppActions(app));
            } else {
                holder.marker.setText("");
                holder.marker.setBackground(null);
                holder.marker.setOnClickListener(null);
            }
            convertView.setOnLongClickListener(v -> {
                if (allAppsAddMode) {
                    return startAllAppsDrag(v, app);
                }
                showAppActions(app);
                return true;
            });
            return convertView;
        }
    }

    private static final class DesktopHolder {
        final ImageView icon;
        final TextView label;

        DesktopHolder(ImageView icon, TextView label) {
            this.icon = icon;
            this.label = label;
        }
    }

    private static final class RowHolder {
        final ImageView icon;
        final TextView label;
        final TextView packageName;
        final TextView marker;

        RowHolder(ImageView icon, TextView label, TextView packageName, TextView marker) {
            this.icon = icon;
            this.label = label;
            this.packageName = packageName;
            this.marker = marker;
        }
    }

    private static final class BackGlyphDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF oval = new RectF();
        private final int color;

        BackGlyphDrawable(int color) {
            this.color = color;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float diameter = size * 0.88f;
            float left = (width - diameter) * 0.5f;
            float top = (height - diameter) * 0.5f;
            oval.set(left, top, left + diameter, top + diameter);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(253, 253, 252));
            canvas.drawOval(oval, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1.05f, size * 0.018f));
            paint.setColor(Color.rgb(222, 224, 226));
            canvas.drawOval(oval, paint);

            float cx = width * 0.51f;
            float cy = height * 0.5f;
            float arm = diameter * 0.155f;
            float reach = diameter * 0.205f;

            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(Math.max(2.1f, size * 0.043f));
            paint.setColor(color);
            canvas.drawLine(cx + reach * 0.34f, cy - arm, cx - reach * 0.72f, cy, paint);
            canvas.drawLine(cx - reach * 0.72f, cy, cx + reach * 0.34f, cy + arm, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SearchGlyphDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF oval = new RectF();

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.43f;
            float cy = height * 0.42f;
            float radius = size * 0.23f;
            float stroke = Math.max(2.05f, size * 0.066f);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(stroke);
            paint.setColor(Color.rgb(125, 132, 138));
            oval.set(cx - radius, cy - radius, cx + radius, cy + radius);
            canvas.drawOval(oval, paint);
            float handleStartX = cx + radius * 0.68f;
            float handleStartY = cy + radius * 0.68f;
            float handleEndX = cx + radius * 1.55f;
            float handleEndY = cy + radius * 1.55f;
            canvas.drawLine(handleStartX, handleStartY, handleEndX, handleEndY, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class ClearGlyphDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.5f;
            float cy = height * 0.5f;
            float radius = size * 0.455f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(247, 248, 248));
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.03f));
            paint.setColor(Color.rgb(224, 226, 228));
            canvas.drawCircle(cx, cy, radius - paint.getStrokeWidth() * 0.5f, paint);

            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(Math.max(1.9f, size * 0.058f));
            paint.setColor(Color.rgb(133, 140, 146));
            float arm = size * 0.17f;
            canvas.drawLine(cx - arm, cy - arm, cx + arm, cy + arm, paint);
            canvas.drawLine(cx + arm, cy - arm, cx - arm, cy + arm, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DesktopLoadingDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private float phase;

        void setPhase(float phase) {
            this.phase = phase - (float) Math.floor(phase);
            invalidateSelf();
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.5f;
            float cy = height * 0.5f;
            float panelW = size * 0.78f;
            float panelH = size * 0.92f;
            float left = cx - panelW * 0.5f;
            float top = cy - panelH * 0.5f;
            float tile = panelW * 0.19f;
            float gap = panelW * 0.065f;
            float gridW = tile * 3f + gap * 2f;
            float gridH = tile * 4f + gap * 3f;
            float gridLeft = cx - gridW * 0.5f;
            float gridTop = cy - gridH * 0.5f - size * 0.02f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(34, 0, 0, 0));
            rect.set(left, top + size * 0.035f, left + panelW, top + panelH + size * 0.035f);
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);

            paint.setShader(new LinearGradient(0, top, 0, top + panelH,
                    Color.argb(220, 250, 252, 253),
                    Color.argb(214, 222, 228, 232),
                    Shader.TileMode.CLAMP));
            rect.set(left, top, left + panelW, top + panelH);
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.014f));
            paint.setColor(Color.argb(150, 180, 187, 192));
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);

            paint.setStyle(Paint.Style.FILL);
            float sweepX = left + panelW * ((phase * 1.18f) % 1.18f) - panelW * 0.12f;
            paint.setShader(new LinearGradient(sweepX, top, sweepX + panelW * 0.24f, top,
                    new int[]{
                            Color.argb(0, 255, 255, 255),
                            Color.argb(72, 255, 255, 255),
                            Color.argb(0, 255, 255, 255)
                    },
                    new float[]{0f, 0.5f, 1f},
                    Shader.TileMode.CLAMP));
            rect.set(left + size * 0.03f, top + size * 0.03f, left + panelW - size * 0.03f, top + panelH - size * 0.03f);
            canvas.drawRoundRect(rect, size * 0.05f, size * 0.05f, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            for (int row = 0; row < 4; row++) {
                for (int column = 0; column < 3; column++) {
                    int index = row * 3 + column;
                    float wave = (float) (0.5f + 0.5f * Math.sin((phase * Math.PI * 2f) - index * 0.58f));
                    float scale = 0.82f + wave * 0.22f;
                    float tileCx = gridLeft + column * (tile + gap) + tile * 0.5f;
                    float tileCy = gridTop + row * (tile + gap) + tile * 0.5f - wave * size * 0.018f;
                    float half = tile * scale * 0.5f;
                    float radius = half * 0.18f;

                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.argb((int) (24 + wave * 26), 0, 0, 0));
                    rect.set(tileCx - half, tileCy - half + size * 0.018f,
                            tileCx + half, tileCy + half + size * 0.018f);
                    canvas.drawRoundRect(rect, radius, radius, paint);

                    paint.setShader(new LinearGradient(0, tileCy - half, 0, tileCy + half,
                            Color.argb((int) (214 + wave * 34), 255, 255, 255),
                            Color.argb((int) (174 + wave * 42), 196, 207, 215),
                            Shader.TileMode.CLAMP));
                    rect.set(tileCx - half, tileCy - half, tileCx + half, tileCy + half);
                    canvas.drawRoundRect(rect, radius, radius, paint);
                    paint.setShader(null);

                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.argb((int) (84 + wave * 76), 138, 149, 158));
                    canvas.drawRoundRect(rect, radius, radius, paint);
                }
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(210, 68, 78, 86));
            rect.set(cx - size * 0.17f, top + panelH * 0.82f, cx + size * 0.17f, top + panelH * 0.87f);
            canvas.drawRoundRect(rect, size * 0.018f, size * 0.018f, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(120, 255, 255, 255));
            canvas.drawCircle(cx + (float) Math.sin(phase * Math.PI * 2f) * size * 0.11f,
                    top + panelH * 0.845f, size * 0.012f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class ActionSheetBackgroundDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(255);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    Color.rgb(43, 49, 54), Color.rgb(23, 28, 32), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, 18f, 18f, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setColor(Color.argb(190, 84, 93, 100));
            rect.inset(1f, 1f);
            canvas.drawRoundRect(rect, 17f, 17f, paint);
            rect.inset(2f, 2f);
            paint.setColor(Color.argb(58, 255, 255, 255));
            paint.setStrokeWidth(1f);
            canvas.drawRoundRect(rect, 15f, 15f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class ActionSheetRowDrawable extends Drawable {
        private final boolean destructive;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        ActionSheetRowDrawable(boolean destructive) {
            this.destructive = destructive;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(destructive ? Color.argb(132, 104, 45, 42) : Color.argb(118, 82, 91, 98));
            canvas.drawRoundRect(rect, 13f, 13f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(destructive ? Color.argb(150, 218, 116, 104) : Color.argb(112, 184, 194, 201));
            rect.inset(0.5f, 0.5f);
            canvas.drawRoundRect(rect, 12.5f, 12.5f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DropTrayDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(255);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    Color.rgb(42, 48, 53), Color.rgb(24, 29, 33), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, 22f, 22f, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1.5f);
            paint.setColor(Color.argb(170, 91, 101, 109));
            rect.inset(1f, 1f);
            canvas.drawRoundRect(rect, 21f, 21f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DropTargetDrawable extends Drawable {
        private final boolean active;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        DropTargetDrawable(boolean active) {
            this.active = active;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(active ? Color.argb(190, 94, 110, 119) : Color.argb(96, 82, 91, 98));
            canvas.drawRoundRect(rect, 18f, 18f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(active ? 2.5f : 1f);
            paint.setColor(active ? Color.rgb(218, 231, 237) : Color.argb(116, 186, 196, 203));
            rect.inset(1f, 1f);
            canvas.drawRoundRect(rect, 17f, 17f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class ActionIconDrawable extends Drawable {
        private final int type;
        private final boolean destructive;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        ActionIconDrawable(int type, boolean destructive) {
            this.type = type;
            this.destructive = destructive;
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.5f;
            float cy = height * 0.5f;
            int accent = destructive ? Color.rgb(232, 126, 112) : Color.rgb(215, 223, 228);
            int muted = destructive ? Color.argb(105, 232, 126, 112) : Color.argb(105, 215, 223, 228);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(destructive ? Color.argb(62, 180, 62, 54) : Color.argb(55, 255, 255, 255));
            canvas.drawCircle(cx, cy, size * 0.46f, paint);

            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(2f, size * 0.065f));
            paint.setColor(accent);
            if (type == ACTION_HOME) {
                float cell = size * 0.16f;
                float gap = size * 0.08f;
                float startX = cx - cell - gap * 0.5f;
                float startY = cy - cell - gap * 0.5f;
                paint.setStyle(Paint.Style.FILL);
                for (int row = 0; row < 2; row++) {
                    for (int column = 0; column < 2; column++) {
                        rect.set(startX + column * (cell + gap), startY + row * (cell + gap),
                                startX + column * (cell + gap) + cell, startY + row * (cell + gap) + cell);
                        canvas.drawRoundRect(rect, cell * 0.18f, cell * 0.18f, paint);
                    }
                }
            } else if (type == ACTION_DOCK) {
                paint.setStyle(Paint.Style.STROKE);
                rect.set(cx - size * 0.28f, cy - size * 0.24f, cx + size * 0.28f, cy + size * 0.24f);
                canvas.drawRoundRect(rect, size * 0.09f, size * 0.09f, paint);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(muted);
                canvas.drawCircle(cx - size * 0.15f, cy + size * 0.09f, size * 0.035f, paint);
                canvas.drawCircle(cx, cy + size * 0.09f, size * 0.035f, paint);
                canvas.drawCircle(cx + size * 0.15f, cy + size * 0.09f, size * 0.035f, paint);
            } else if (type == ACTION_INFO) {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(cx, cy, size * 0.24f, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy - size * 0.12f, size * 0.035f, paint);
                rect.set(cx - size * 0.025f, cy - size * 0.04f, cx + size * 0.025f, cy + size * 0.17f);
                canvas.drawRoundRect(rect, size * 0.02f, size * 0.02f, paint);
            } else if (type == ACTION_HIDE) {
                paint.setStyle(Paint.Style.STROKE);
                rect.set(cx - size * 0.28f, cy - size * 0.16f, cx + size * 0.28f, cy + size * 0.16f);
                canvas.drawOval(rect, paint);
                canvas.drawCircle(cx, cy, size * 0.08f, paint);
                canvas.drawLine(cx - size * 0.25f, cy + size * 0.25f, cx + size * 0.25f, cy - size * 0.25f, paint);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                rect.set(cx - size * 0.18f, cy - size * 0.12f, cx + size * 0.18f, cy + size * 0.24f);
                canvas.drawRoundRect(rect, size * 0.05f, size * 0.05f, paint);
                canvas.drawLine(cx - size * 0.22f, cy - size * 0.2f, cx + size * 0.22f, cy - size * 0.2f, paint);
                canvas.drawLine(cx - size * 0.08f, cy - size * 0.28f, cx + size * 0.08f, cy - size * 0.28f, paint);
            }
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsPageBackgroundDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(244, 244, 241));
            canvas.drawRect(0, 0, width, height, paint);

            paint.setStrokeWidth(1f);
            for (int x = 0; x < width; x += Math.max(7, width / 82)) {
                paint.setColor(Color.argb(x % 3 == 0 ? 9 : 5, 210, 211, 206));
                canvas.drawLine(x, 0, x, height, paint);
            }
            for (int y = 0; y < height; y += Math.max(8, height / 96)) {
                paint.setColor(Color.argb(y % 4 == 0 ? 7 : 4, 255, 255, 255));
                canvas.drawLine(0, y, width, y, paint);
            }
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class SettingsCardDrawable extends Drawable {
        private final int radius;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        SettingsCardDrawable(int radius) {
            this.radius = radius;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float inset = Math.max(1f, radius * 0.12f);
            rect.set(inset, inset + 1f, width - inset, height - inset + 1f);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(24, 0, 0, 0));
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setAlpha(255);
            rect.set(inset, inset, width - inset, height - inset - 1f);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    Color.rgb(255, 255, 255), Color.rgb(250, 250, 248), Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.rgb(218, 219, 216));
            canvas.drawRoundRect(rect, radius, radius, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class ThemeChoiceCardDrawable extends Drawable {
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        ThemeChoiceCardDrawable(boolean selected) {
            this.selected = selected;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float radius = Math.max(6f, Math.min(width, height) * 0.045f);
            float inset = selected ? 2.5f : 1.5f;
            rect.set(inset, inset + 1f, width - inset, height - inset + 1f);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(selected ? 34 : 22, 0, 0, 0));
            canvas.drawRoundRect(rect, radius, radius, paint);

            rect.set(inset, inset, width - inset, height - inset - 1f);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    selected ? Color.rgb(255, 255, 253) : Color.rgb(255, 255, 255),
                    selected ? Color.rgb(251, 247, 244) : Color.rgb(250, 250, 248),
                    Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(selected ? 3f : 1f);
            paint.setColor(selected ? Color.rgb(199, 89, 78) : Color.rgb(219, 220, 217));
            canvas.drawRoundRect(rect, radius, radius, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class ThemePreviewDrawable extends Drawable {
        private final ThemePalette palette;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        ThemePreviewDrawable(ThemePalette palette, boolean selected) {
            this.palette = palette;
            this.selected = selected;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float inset = Math.max(2f, Math.min(width, height) * 0.035f);
            float radius = Math.max(5f, Math.min(width, height) * 0.07f);
            rect.set(inset, inset + 1f, width - inset, height - inset + 1f);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(42, 0, 0, 0));
            canvas.drawRoundRect(rect, radius, radius, paint);

            rect.set(inset, inset, width - inset, height - inset);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    palette.topColor, palette.bottomColor, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            float panelLeft = rect.left;
            float panelTop = rect.top;
            float panelWidth = rect.width();
            float panelHeight = rect.height();
            int columns = 4;
            int rows = 5;
            float gridTop = panelTop + panelHeight * 0.12f;
            float dockTop = panelTop + panelHeight * 0.74f;
            float gridHeight = Math.max(1f, dockTop - gridTop - panelHeight * 0.08f);
            float maxGridWidth = panelWidth * 0.46f;
            float maxGridHeight = gridHeight * 0.92f;
            float cell = Math.min(
                    maxGridWidth / (columns + (columns - 1) * 0.34f),
                    maxGridHeight / (rows + (rows - 1) * 0.34f)
            );
            float gap = Math.max(2f, cell * 0.34f);
            float gridWidth = columns * cell + (columns - 1) * gap;
            float actualGridHeight = rows * cell + (rows - 1) * gap;
            float gridLeft = panelLeft + (panelWidth - gridWidth) * 0.5f;
            gridTop += (gridHeight - actualGridHeight) * 0.5f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(palette.cellColor);
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    float left = gridLeft + column * (cell + gap);
                    float top = gridTop + row * (cell + gap);
                    rect.set(left, top, left + cell, top + cell);
                    canvas.drawRoundRect(rect, Math.max(1.5f, cell * 0.18f), Math.max(1.5f, cell * 0.18f), paint);
                }
            }

            rect.set(panelLeft, dockTop, panelLeft + panelWidth, panelTop + panelHeight);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    blendColor(palette.dockColor, Color.WHITE, palette.light ? 0.24f : 0.10f),
                    palette.dockColor, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius * 0.45f, radius * 0.45f, paint);
            paint.setShader(null);

            float dockHeight = Math.max(1f, panelTop + panelHeight - dockTop);
            float dockIcon = Math.min(Math.min(cell * 1.08f, dockHeight * 0.42f), panelWidth * 0.10f);
            float dockGap = Math.max(dockIcon * 1.2f, panelWidth * 0.08f);
            float dockWidth = dockIcon * 3f + dockGap * 2f;
            float dockLeft = panelLeft + (panelWidth - dockWidth) * 0.5f;
            float dockIconTop = dockTop + (dockHeight - dockIcon) * 0.52f;
            paint.setColor(blendColor(palette.accentColor, Color.WHITE, 0.34f));
            for (int i = 0; i < 3; i++) {
                float left = dockLeft + i * (dockIcon + dockGap);
                rect.set(left, dockIconTop, left + dockIcon, dockIconTop + dockIcon);
                canvas.drawRoundRect(rect, Math.max(1.5f, dockIcon * 0.24f), Math.max(1.5f, dockIcon * 0.24f), paint);
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(selected ? 3f : 1.2f);
            paint.setColor(selected ? palette.accentColor
                    : (palette.light ? Color.argb(150, 120, 130, 136) : Color.argb(120, 255, 255, 255)));
            rect.set(inset + 0.5f, inset + 0.5f, width - inset - 0.5f, height - inset - 0.5f);
            canvas.drawRoundRect(rect, radius, radius, paint);
            canvas.restore();
        }

        private static int blendColor(int from, int to, float amount) {
            float clamped = Math.max(0f, Math.min(1f, amount));
            int a = Math.round(Color.alpha(from) + (Color.alpha(to) - Color.alpha(from)) * clamped);
            int r = Math.round(Color.red(from) + (Color.red(to) - Color.red(from)) * clamped);
            int g = Math.round(Color.green(from) + (Color.green(to) - Color.green(from)) * clamped);
            int b = Math.round(Color.blue(from) + (Color.blue(to) - Color.blue(from)) * clamped);
            return Color.argb(a, r, g, b);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsCheckDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.5f;
            float cy = height * 0.5f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(44, 0, 0, 0));
            canvas.drawCircle(cx, cy + size * 0.04f, size * 0.46f, paint);
            paint.setAlpha(255);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    Color.rgb(213, 98, 87), Color.rgb(180, 73, 66), Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, cy, size * 0.44f, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(Math.max(2.4f, size * 0.11f));
            paint.setColor(Color.WHITE);
            path.reset();
            path.moveTo(width * 0.30f, height * 0.50f);
            path.lineTo(width * 0.44f, height * 0.64f);
            path.lineTo(width * 0.72f, height * 0.36f);
            canvas.drawPath(path, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsSwitchDrawable extends Drawable {
        private final boolean checked;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        SettingsSwitchDrawable(boolean checked) {
            this.checked = checked;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float sideInset = Math.max(2f, height * 0.06f);
            float verticalInset = Math.max(3f, height * 0.13f);
            float radius = (height - verticalInset * 2f) * 0.5f;
            rect.set(sideInset, verticalInset + height * 0.04f,
                    width - sideInset, height - verticalInset + height * 0.04f);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(28, 0, 0, 0));
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setAlpha(255);
            rect.set(sideInset, verticalInset, width - sideInset, height - verticalInset);
            int topColor = checked ? Color.rgb(99, 142, 213) : Color.rgb(247, 248, 247);
            int bottomColor = checked ? Color.rgb(67, 113, 196) : Color.rgb(236, 237, 235);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    topColor, bottomColor, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(checked ? Color.rgb(58, 99, 174) : Color.rgb(220, 222, 220));
            canvas.drawRoundRect(rect, radius, radius, paint);

            float knobRadius = radius - Math.max(2f, height * 0.045f);
            float knobCx = checked ? width - sideInset - radius : sideInset + radius;
            float knobCy = height * 0.5f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(42, 0, 0, 0));
            canvas.drawCircle(knobCx, knobCy + height * 0.04f, knobRadius * 0.98f, paint);
            paint.setAlpha(255);
            paint.setShader(new LinearGradient(0, knobCy - knobRadius, 0, knobCy + knobRadius,
                    Color.rgb(255, 255, 255), Color.rgb(235, 236, 234), Shader.TileMode.CLAMP));
            canvas.drawCircle(knobCx, knobCy, knobRadius, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(checked ? Color.rgb(219, 229, 247) : Color.rgb(215, 216, 214));
            paint.setStrokeWidth(1f);
            canvas.drawCircle(knobCx, knobCy, knobRadius, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsHeaderDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    Color.rgb(253, 253, 251), Color.rgb(242, 242, 238), Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.rgb(218, 219, 216));
            canvas.drawLine(0, height - 0.5f, width, height - 0.5f, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class SettingsGridPreviewDrawable extends Drawable {
        private final int mode;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        SettingsGridPreviewDrawable(int mode, boolean selected) {
            this.mode = mode;
            this.selected = selected;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float panelH = height * 0.88f;
            float panelW = panelH * (mode == 20 ? 0.48f : 0.55f);
            panelW = Math.min(panelW, width * 0.62f);
            float left = (width - panelW) * 0.5f;
            float top = (height - panelH) * 0.5f;
            float radius = Math.max(1.5f, panelW * 0.025f);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(30, 0, 0, 0));
            rect.set(left - panelW * 0.015f, top + panelH * 0.02f,
                    left + panelW + panelW * 0.015f, top + panelH + panelH * 0.025f);
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setAlpha(255);
            paint.setShader(new LinearGradient(0, top, 0, top + panelH,
                    Color.rgb(214, 216, 217), Color.rgb(196, 198, 199), Shader.TileMode.CLAMP));
            rect.set(left, top, left + panelW, top + panelH);
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, panelW * 0.018f));
            paint.setColor(selected ? Color.rgb(186, 188, 188) : Color.rgb(188, 190, 191));
            canvas.drawRoundRect(rect, radius, radius, paint);

            int columns = mode == 20 ? 4 : 3;
            int rows = mode == 20 ? 5 : 4;
            float sideInset = panelW * 0.13f;
            float gridTop = top + panelH * 0.11f;
            float gridBottom = top + panelH * 0.73f;
            float gap = panelW * 0.075f;
            float tileW = (panelW - sideInset * 2f - gap * (columns - 1)) / columns;
            float tileH = (gridBottom - gridTop - gap * (rows - 1)) / rows;
            float tile = Math.min(tileW, tileH);
            paint.setStyle(Paint.Style.FILL);
            for (int row = 0; row < rows; row++) {
                for (int column = 0; column < columns; column++) {
                    float cellLeft = left + sideInset + column * (tile + gap);
                    float cellTop = gridTop + row * (tile + gap);
                    paint.setColor(Color.rgb(226, 228, 228));
                    rect.set(cellLeft, cellTop, cellLeft + tile, cellTop + tile);
                    canvas.drawRoundRect(rect, tile * 0.06f, tile * 0.06f, paint);
                }
            }

            paint.setColor(Color.rgb(183, 185, 186));
            rect.set(left, top + panelH * 0.78f, left + panelW, top + panelH);
            canvas.drawRect(rect, paint);
            float dockTile = Math.min(tile, panelW * 0.18f);
            float dockGap = panelW * 0.085f;
            float dockWidth = dockTile * columns + dockGap * (columns - 1);
            float dockLeft = left + (panelW - dockWidth) * 0.5f;
            float dockTop = top + panelH * 0.835f;
            paint.setColor(Color.rgb(246, 247, 247));
            for (int column = 0; column < columns; column++) {
                float cellLeft = dockLeft + column * (dockTile + dockGap);
                rect.set(cellLeft, dockTop, cellLeft + dockTile, dockTop + dockTile);
                canvas.drawRoundRect(rect, dockTile * 0.06f, dockTile * 0.06f, paint);
            }
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsArrowDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        SettingsArrowDrawable() {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float cx = width * 0.5f;
            float cy = height * 0.5f;
            paint.setAlpha(255);
            paint.setStrokeWidth(Math.max(2f, size * 0.09f));
            paint.setColor(Color.rgb(172, 177, 181));
            path.reset();
            path.moveTo(cx - size * 0.10f, cy - size * 0.20f);
            path.lineTo(cx + size * 0.12f, cy);
            path.lineTo(cx - size * 0.10f, cy + size * 0.20f);
            canvas.drawPath(path, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class SettingsItemIconDrawable extends Drawable {
        private final String kind;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final RectF rect = new RectF();

        SettingsItemIconDrawable(String kind) {
            this.kind = kind == null ? "" : kind;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float size = Math.min(width, height);
            float inset = size * 0.06f;
            float radius = size * 0.14f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(28, 0, 0, 0));
            rect.set(inset, inset + size * 0.03f, width - inset, height - inset + size * 0.03f);
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setColor(Color.rgb(238, 242, 244));
            rect.set(inset, inset, width - inset, height - inset);
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.025f));
            paint.setColor(Color.rgb(214, 219, 222));
            canvas.drawRoundRect(rect, radius, radius, paint);

            float cx = width * 0.5f;
            float cy = height * 0.5f;
            paint.setStrokeWidth(size * 0.07f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Color.rgb(104, 111, 116));
            paint.setStyle(Paint.Style.STROKE);

            if ("theme".equals(kind)) {
                drawTheme(canvas, cx, cy, size);
            } else if ("wallpaper".equals(kind)) {
                drawWallpaper(canvas, cx, cy, size);
            } else if ("animation".equals(kind)) {
                drawAnimation(canvas, cx, cy, size);
            } else if ("icon".equals(kind)) {
                drawIcon(canvas, cx, cy, size);
            } else if ("manage".equals(kind)) {
                drawManage(canvas, size);
            } else if ("size".equals(kind)) {
                drawSize(canvas, size);
            } else if ("text".equals(kind)) {
                drawText(canvas, size);
            } else if ("sort".equals(kind)) {
                drawSort(canvas, size);
            } else if ("hide".equals(kind)) {
                drawHide(canvas, cx, cy, size);
            } else if ("quick".equals(kind)) {
                drawQuick(canvas, cx, cy, size);
            } else if ("history".equals(kind)) {
                drawHistory(canvas, cx, cy, size);
            } else if ("reset".equals(kind)) {
                drawReset(canvas, cx, cy, size);
            } else if ("version".equals(kind)) {
                drawVersion(canvas, cx, cy, size);
            } else if ("power".equals(kind)) {
                drawPower(canvas, size);
            } else if ("about".equals(kind)) {
                drawAbout(canvas, cx, cy, size);
            } else {
                drawRefresh(canvas, cx, cy, size);
            }

            canvas.restore();
        }

        private void drawTheme(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(78, 127, 184));
            canvas.drawCircle(cx - size * 0.11f, cy - size * 0.08f, size * 0.115f, paint);
            paint.setColor(Color.rgb(201, 88, 78));
            canvas.drawCircle(cx + size * 0.10f, cy - size * 0.07f, size * 0.105f, paint);
            paint.setColor(Color.rgb(84, 158, 107));
            canvas.drawCircle(cx, cy + size * 0.13f, size * 0.11f, paint);
        }

        private void drawWallpaper(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.28f, cy - size * 0.22f, cx + size * 0.28f, cy + size * 0.22f);
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx + size * 0.13f, cy - size * 0.09f, size * 0.045f, paint);
            path.reset();
            path.moveTo(cx - size * 0.22f, cy + size * 0.16f);
            path.lineTo(cx - size * 0.06f, cy - size * 0.02f);
            path.lineTo(cx + size * 0.04f, cy + size * 0.08f);
            path.lineTo(cx + size * 0.13f, cy - size * 0.01f);
            path.lineTo(cx + size * 0.25f, cy + size * 0.16f);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawAnimation(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.25f, cy - size * 0.18f, cx + size * 0.05f, cy + size * 0.12f);
            canvas.drawRoundRect(rect, size * 0.035f, size * 0.035f, paint);
            rect.offset(size * 0.16f, size * 0.11f);
            canvas.drawRoundRect(rect, size * 0.035f, size * 0.035f, paint);
        }

        private void drawIcon(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.20f, cy - size * 0.20f, cx + size * 0.20f, cy + size * 0.20f);
            canvas.drawRoundRect(rect, size * 0.08f, size * 0.08f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy, size * 0.055f, paint);
        }

        private void drawManage(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < 3; i++) {
                float y = size * (0.32f + i * 0.17f);
                canvas.drawLine(size * 0.27f, y, size * 0.73f, y, paint);
                paint.setStyle(Paint.Style.FILL);
                float x = size * (i == 1 ? 0.42f : 0.58f);
                canvas.drawCircle(x, y, size * 0.045f, paint);
                paint.setStyle(Paint.Style.STROKE);
            }
        }

        private void drawSize(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(size * 0.24f, size * 0.36f, size * 0.45f, size * 0.57f);
            canvas.drawRoundRect(rect, size * 0.035f, size * 0.035f, paint);
            rect.set(size * 0.50f, size * 0.25f, size * 0.76f, size * 0.51f);
            canvas.drawRoundRect(rect, size * 0.04f, size * 0.04f, paint);
        }

        private void drawText(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(size * 0.25f, size * 0.34f, size * 0.75f, size * 0.34f, paint);
            canvas.drawLine(size * 0.50f, size * 0.34f, size * 0.50f, size * 0.70f, paint);
            canvas.drawLine(size * 0.38f, size * 0.70f, size * 0.62f, size * 0.70f, paint);
        }

        private void drawSort(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            for (int i = 0; i < 3; i++) {
                float y = size * (0.32f + i * 0.17f);
                canvas.drawCircle(size * 0.28f, y, size * 0.018f, paint);
                canvas.drawLine(size * 0.40f, y, size * 0.74f, y, paint);
            }
        }

        private void drawHide(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.28f, cy - size * 0.16f, cx + size * 0.28f, cy + size * 0.16f);
            canvas.drawOval(rect, paint);
            canvas.drawCircle(cx, cy, size * 0.06f, paint);
            canvas.drawLine(cx - size * 0.24f, cy + size * 0.24f, cx + size * 0.24f, cy - size * 0.24f, paint);
        }

        private void drawQuick(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.FILL);
            path.reset();
            path.moveTo(cx + size * 0.04f, cy - size * 0.30f);
            path.lineTo(cx - size * 0.18f, cy + size * 0.04f);
            path.lineTo(cx + size * 0.02f, cy + size * 0.04f);
            path.lineTo(cx - size * 0.03f, cy + size * 0.30f);
            path.lineTo(cx + size * 0.22f, cy - size * 0.07f);
            path.lineTo(cx + size * 0.02f, cy - size * 0.07f);
            path.close();
            canvas.drawPath(path, paint);
        }

        private void drawHistory(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, size * 0.24f, paint);
            canvas.drawLine(cx, cy, cx, cy - size * 0.13f, paint);
            canvas.drawLine(cx, cy, cx + size * 0.12f, cy + size * 0.08f, paint);
        }

        private void drawReset(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.22f, cy - size * 0.22f, cx + size * 0.22f, cy + size * 0.22f);
            canvas.drawArc(rect, 30f, 285f, false, paint);
            path.reset();
            path.moveTo(cx + size * 0.23f, cy - size * 0.15f);
            path.lineTo(cx + size * 0.23f, cy - size * 0.02f);
            path.lineTo(cx + size * 0.11f, cy - size * 0.08f);
            canvas.drawPath(path, paint);
        }

        private void drawVersion(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.23f, cy - size * 0.25f, cx + size * 0.23f, cy + size * 0.25f);
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);
            canvas.drawLine(cx - size * 0.14f, cy - size * 0.08f, cx + size * 0.14f, cy - size * 0.08f, paint);
            canvas.drawLine(cx - size * 0.14f, cy + size * 0.07f, cx + size * 0.07f, cy + size * 0.07f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx + size * 0.14f, cy + size * 0.07f, size * 0.025f, paint);
        }

        private void drawPower(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(size * 0.30f, size * 0.30f, size * 0.70f, size * 0.62f);
            canvas.drawRoundRect(rect, size * 0.06f, size * 0.06f, paint);
            canvas.drawLine(size * 0.72f, size * 0.40f, size * 0.78f, size * 0.40f, paint);
            paint.setStyle(Paint.Style.FILL);
            rect.set(size * 0.35f, size * 0.35f, size * 0.50f, size * 0.57f);
            canvas.drawRoundRect(rect, size * 0.03f, size * 0.03f, paint);
        }

        private void drawAbout(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(cx, cy, size * 0.24f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy - size * 0.11f, size * 0.035f, paint);
            rect.set(cx - size * 0.025f, cy - size * 0.02f, cx + size * 0.025f, cy + size * 0.15f);
            canvas.drawRoundRect(rect, size * 0.02f, size * 0.02f, paint);
        }

        private void drawRefresh(Canvas canvas, float cx, float cy, float size) {
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - size * 0.22f, cy - size * 0.22f, cx + size * 0.22f, cy + size * 0.22f);
            canvas.drawArc(rect, 210f, 220f, false, paint);
            path.reset();
            path.moveTo(cx + size * 0.21f, cy - size * 0.19f);
            path.lineTo(cx + size * 0.26f, cy - size * 0.05f);
            path.lineTo(cx + size * 0.10f, cy - size * 0.07f);
            canvas.drawPath(path, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DesktopPageIndicatorDotDrawable extends Drawable {
        private final boolean active;
        private final boolean editing;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        DesktopPageIndicatorDotDrawable(boolean active, boolean editing) {
            this.active = active;
            this.editing = editing;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float radius = height * 0.5f;
            rect.set(0.5f, 0.5f, width - 0.5f, height - 0.5f);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(active ? 88 : 58, 0, 0, 0));
            rect.offset(0f, Math.max(1f, height * 0.16f));
            canvas.drawRoundRect(rect, radius, radius, paint);
            rect.offset(0f, -Math.max(1f, height * 0.16f));

            paint.setAlpha(255);
            if (editing && active) {
                paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                        Color.rgb(226, 88, 75), Color.rgb(179, 55, 48), Shader.TileMode.CLAMP));
            } else {
                int topAlpha = active ? 248 : 210;
                int bottomAlpha = active ? 232 : 172;
                paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                        Color.argb(topAlpha, 255, 255, 255),
                        Color.argb(bottomAlpha, 205, 211, 214), Shader.TileMode.CLAMP));
            }
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(editing && active
                    ? Color.argb(180, 120, 34, 30)
                    : Color.argb(active ? 150 : 92, 255, 255, 255));
            canvas.drawRoundRect(rect, radius, radius, paint);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class PageSwitchDepthDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, width, 0,
                    new int[]{
                            Color.argb(70, 0, 0, 0),
                            Color.argb(0, 0, 0, 0),
                            Color.argb(0, 0, 0, 0),
                            Color.argb(82, 0, 0, 0)
                    },
                    new float[]{0f, 0.16f, 0.78f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setShader(new LinearGradient(0, 0, 0, height,
                    Color.argb(34, 255, 255, 255),
                    Color.argb(34, 0, 0, 0),
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DesktopBackdropDrawable extends Drawable {
        private final int topColor;
        private final int bottomColor;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        DesktopBackdropDrawable(int topColor, int bottomColor) {
            this.topColor = topColor;
            this.bottomColor = bottomColor;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    topColor, bottomColor, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            for (int y = 0; y < height; y += Math.max(5, height / 90)) {
                int alpha = 5 + ((y / Math.max(1, height / 30)) % 3);
                paint.setColor(Color.argb(alpha, 255, 255, 255));
                canvas.drawLine(0, y, width, y, paint);
            }

            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class FrostedWallpaperDrawable extends Drawable {
        private final Bitmap wallpaperBitmap;
        private final int fallbackTopColor;
        private final int fallbackBottomColor;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        private final RectF rect = new RectF();
        private int alpha = 255;

        FrostedWallpaperDrawable(Bitmap wallpaperBitmap, int fallbackTopColor, int fallbackBottomColor) {
            this.wallpaperBitmap = wallpaperBitmap;
            this.fallbackTopColor = fallbackTopColor;
            this.fallbackBottomColor = fallbackBottomColor;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);

            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(alpha);
            paint.setColorFilter(null);
            if (wallpaperBitmap != null && !wallpaperBitmap.isRecycled()) {
                canvas.drawBitmap(wallpaperBitmap, null, rect, paint);
            } else {
                paint.setShader(new LinearGradient(0, 0, 0, height,
                        fallbackTopColor, fallbackBottomColor, Shader.TileMode.CLAMP));
                canvas.drawRect(rect, paint);
                paint.setShader(null);
            }

            paint.setAlpha(alpha);
            paint.setColor(Color.argb(148, 15, 18, 21));
            canvas.drawRect(rect, paint);

            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{
                            Color.argb(74, 255, 255, 255),
                            Color.argb(10, 255, 255, 255),
                            Color.argb(138, 0, 0, 0)
                    },
                    new float[]{0f, 0.46f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRect(rect, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            for (int y = 0; y < height; y += Math.max(6, height / 96)) {
                paint.setColor(Color.argb(5, 255, 255, 255));
                canvas.drawLine(0, y, width, y, paint);
            }
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class TransparentCellDrawable extends Drawable {
        private final int position;
        private final boolean selected;
        private final int mode;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();
        private int alpha = 255;

        TransparentCellDrawable(int position, boolean selected, int mode) {
            this.position = position;
            this.selected = selected;
            this.mode = mode;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float inset = Math.max(1.5f, Math.min(width, height) * (mode == 20 ? 0.028f : 0.024f));
            float radius = Math.max(4f, Math.min(width, height) * 0.055f);
            rect.set(inset, inset, width - inset, height - inset);

            paint.setAlpha(alpha);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, rect.top, 0, rect.bottom,
                    new int[]{
                            Color.argb(86, 255, 255, 255),
                            Color.argb(44, 225, 234, 242),
                            Color.argb(42, 7, 10, 12)
                    },
                    new float[]{0f, 0.54f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.argb(76, 255, 255, 255));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setColor(Color.argb(80, 0, 0, 0));
            rect.inset(0.5f, 0.5f);
            canvas.drawRoundRect(rect, radius, radius, paint);
            rect.inset(-0.5f, -0.5f);

            paint.setStyle(Paint.Style.FILL);
            float shineTop = rect.top + (position % Math.max(1, mode == 20 ? 4 : 3)) * Math.max(1f, rect.height() * 0.02f);
            paint.setShader(new LinearGradient(0, shineTop, 0, shineTop + rect.height() * 0.36f,
                    Color.argb(36, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);
            paint.setShader(null);

            if (selected) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(62, 205, 95, 82));
                canvas.drawRoundRect(rect, radius, radius, paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(2f, Math.min(width, height) * 0.014f));
                paint.setColor(Color.argb(214, 232, 126, 111));
                canvas.drawRoundRect(rect, radius, radius, paint);
            }
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class TransparentDockDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private int alpha = 255;

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setAlpha(alpha);
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{
                            Color.argb(94, 255, 255, 255),
                            Color.argb(72, 31, 37, 43),
                            Color.argb(168, 5, 8, 11)
                    },
                    new float[]{0f, 0.34f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            paint.setColor(Color.argb(142, 255, 255, 255));
            canvas.drawLine(0, 0.5f, width, 0.5f, paint);
            paint.setColor(Color.argb(128, 0, 0, 0));
            canvas.drawLine(0, height - 0.5f, width, height - 0.5f, paint);

            paint.setShader(new LinearGradient(0, 0, 0, Math.max(1f, height * 0.38f),
                    Color.argb(72, 255, 255, 255), Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height * 0.38f, paint);
            paint.setShader(null);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DesktopCellDrawable extends Drawable {
        private final int theme;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        DesktopCellDrawable(int theme, boolean selected) {
            this.theme = theme;
            this.selected = selected;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getMiddleColor());
            canvas.drawRect(0, 0, width, height, paint);

            paint.setStrokeWidth(1f);
            for (int x = Math.max(1, width / 12); x < width; x += Math.max(8, width / 6)) {
                paint.setColor(Color.argb(5, 255, 255, 255));
                canvas.drawLine(x, 0, x, height, paint);
            }
            for (int y = Math.max(1, height / 14); y < height; y += Math.max(9, height / 7)) {
                paint.setColor(Color.argb(3, 0, 0, 0));
                canvas.drawLine(0, y, width, y, paint);
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.argb(26, 112, 119, 124));
            canvas.drawLine(0.5f, 0, width - 0.5f, 0, paint);
            canvas.drawLine(0, 0.5f, 0, height - 0.5f, paint);

            paint.setColor(Color.argb(62, 15, 18, 21));
            canvas.drawLine(width - 0.5f, 0, width - 0.5f, height, paint);
            canvas.drawLine(0, height - 0.5f, width, height - 0.5f, paint);

            if (selected) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(40, 205, 95, 82));
                canvas.drawRect(0, 0, width, height, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(2f, Math.min(width, height) * 0.012f));
                paint.setColor(Color.argb(190, 220, 112, 98));
                rect.set(1.5f, 1.5f, width - 1.5f, height - 1.5f);
                canvas.drawRect(rect, paint);
            }

            canvas.restore();
        }

        private int getTopColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneCellColor(theme, 0);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(60, 51, 47);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(37, 57, 75);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(40, 46, 50);
            }
            return Color.rgb(49, 54, 58);
        }

        private int getMiddleColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneCellColor(theme, 1);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(46, 40, 38);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(29, 45, 62);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(35, 40, 44);
            }
            return Color.rgb(43, 48, 52);
        }

        private int getBottomColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneCellColor(theme, 2);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(39, 35, 34);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(23, 36, 51);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(30, 35, 39);
            }
            return Color.rgb(38, 43, 47);
        }

        private static int getPantoneCellColor(int theme, int stop) {
            ThemePalette palette = getPantonePalette(theme);
            if (palette == null) {
                return Color.rgb(43, 48, 52);
            }
            if (stop == 0) {
                return mixColor(palette.cellColor, Color.WHITE, palette.light ? 0.08f : 0.04f);
            }
            if (stop == 1) {
                return palette.cellColor;
            }
            return mixColor(palette.cellColor, Color.BLACK, palette.light ? 0.18f : 0.28f);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class ClassicCellTextureDrawable extends Drawable {
        private final Bitmap baseBitmap;
        private final Bitmap selectedBitmap;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        private final RectF rect = new RectF();
        private int alpha = 255;
        private ColorFilter colorFilter;

        ClassicCellTextureDrawable(Resources resources, String basePath, String selectedPath, boolean selected) {
            this.baseBitmap = ThemeTextureDrawable.getBitmap(resources, basePath);
            this.selectedBitmap = selected ? ThemeTextureDrawable.getBitmap(resources, selectedPath) : null;
            this.selected = selected;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);

            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(alpha);
            paint.setColorFilter(null);
            paint.setColor(Color.rgb(1, 5, 8));
            canvas.drawRect(rect, paint);

            paint.setColorFilter(colorFilter);
            if (baseBitmap != null && !baseBitmap.isRecycled()) {
                canvas.drawBitmap(baseBitmap, null, rect, paint);
            } else {
                paint.setColor(Color.rgb(47, 50, 54));
                canvas.drawRect(rect, paint);
            }
            paint.setColorFilter(null);
            paint.setAlpha(255);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1f);
            paint.setColor(Color.argb(16, 124, 130, 136));
            canvas.drawLine(0.5f, 0, width - 0.5f, 0, paint);
            canvas.drawLine(0, 0.5f, 0, height - 0.5f, paint);
            paint.setColor(Color.argb(34, 14, 17, 20));
            canvas.drawLine(width - 0.5f, 0, width - 0.5f, height, paint);
            canvas.drawLine(0, height - 0.5f, width, height - 0.5f, paint);

            if (selected) {
                if (selectedBitmap != null && !selectedBitmap.isRecycled()) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setAlpha(210);
                    canvas.drawBitmap(selectedBitmap, null, rect, paint);
                    paint.setAlpha(255);
                }
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.argb(28, 205, 95, 82));
                canvas.drawRect(0, 0, width, height, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(2f, Math.min(width, height) * 0.010f));
                paint.setColor(Color.argb(170, 220, 112, 98));
                rect.set(1.5f, 1.5f, width - 1.5f, height - 1.5f);
                canvas.drawRect(rect, paint);
            }

            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.colorFilter = colorFilter;
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class ThemeTextureDrawable extends Drawable {
        private static final int TEXTURE_CACHE_MAX_BYTES = 24 * 1024 * 1024;
        private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<String, Bitmap>(TEXTURE_CACHE_MAX_BYTES) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value == null ? 0 : value.getByteCount();
            }
        };
        private final Bitmap bitmap;
        private final boolean selected;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
        private final RectF rect = new RectF();
        private int alpha = 255;
        private ColorFilter colorFilter;

        ThemeTextureDrawable(Resources resources, String assetPath, boolean selected) {
            this.bitmap = getBitmap(resources, assetPath);
            this.selected = selected;
        }

        static void preloadBitmap(Resources resources, String assetPath) {
            getBitmap(resources, assetPath);
        }

        private static Bitmap getBitmap(Resources resources, String assetPath) {
            synchronized (BITMAP_CACHE) {
                Bitmap cached = BITMAP_CACHE.get(assetPath);
                if (cached == null || cached.isRecycled()) {
                    try (InputStream inputStream = resources.getAssets().open(assetPath)) {
                        cached = BitmapFactory.decodeStream(inputStream);
                        if (cached != null) {
                            BITMAP_CACHE.put(assetPath, cached);
                        }
                    } catch (Throwable ignored) {
                        cached = null;
                    }
                }
                return cached;
            }
        }

        static void clearTextureCache() {
            synchronized (BITMAP_CACHE) {
                BITMAP_CACHE.evictAll();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            rect.set(0, 0, width, height);

            if (bitmap != null && !bitmap.isRecycled()) {
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(alpha);
                paint.setColorFilter(colorFilter);
                canvas.drawBitmap(bitmap, null, rect, paint);
            } else {
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(alpha);
                paint.setColorFilter(colorFilter);
                paint.setShader(new LinearGradient(0, 0, 0, height,
                        Color.rgb(36, 59, 83), Color.rgb(20, 35, 52), Shader.TileMode.CLAMP));
                canvas.drawRect(rect, paint);
                paint.setShader(null);
            }

            if (selected) {
                paint.setStyle(Paint.Style.FILL);
                paint.setAlpha(255);
                paint.setColorFilter(null);
                paint.setColor(Color.argb(40, 205, 95, 82));
                canvas.drawRect(rect, paint);

                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(Math.max(2f, Math.min(width, height) * 0.012f));
                paint.setColor(Color.argb(190, 220, 112, 98));
                rect.set(1.5f, 1.5f, width - 1.5f, height - 1.5f);
                canvas.drawRect(rect, paint);
            }

            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.colorFilter = colorFilter;
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DesktopDockDrawable extends Drawable {
        private final int theme;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        DesktopDockDrawable(int theme) {
            this.theme = theme;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{getTopColor(), getMiddleColor(), getBottomColor()},
                    new float[]{0f, 0.52f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            paint.setColor(Color.argb(210, 12, 15, 18));
            canvas.drawLine(0, 0.5f, width, 0.5f, paint);
            paint.setColor(Color.argb(64, 94, 100, 105));
            canvas.drawLine(0, 1.5f, width, 1.5f, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, 0, 0, Math.max(1f, height * 0.24f),
                    Color.argb(64, 0, 0, 0), Color.TRANSPARENT, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height * 0.24f, paint);
            paint.setShader(null);

            paint.setStrokeWidth(1f);
            for (int y = Math.max(3, height / 12); y < height; y += Math.max(8, height / 8)) {
                paint.setColor(Color.argb(5, 255, 255, 255));
                canvas.drawLine(0, y, width, y, paint);
            }

            canvas.restore();
        }

        private int getTopColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneDockColor(theme, 0);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(41, 36, 34);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(25, 39, 54);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(28, 33, 37);
            }
            return Color.rgb(33, 38, 42);
        }

        private int getMiddleColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneDockColor(theme, 1);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(34, 31, 30);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(20, 32, 45);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(23, 28, 32);
            }
            return Color.rgb(27, 32, 36);
        }

        private int getBottomColor() {
            if (isPantoneThemeValue(theme)) {
                return getPantoneDockColor(theme, 2);
            }
            if (theme == THEME_COPPER) {
                return Color.rgb(27, 25, 25);
            }
            if (theme == THEME_ORIGINAL_BLUE) {
                return Color.rgb(15, 25, 36);
            }
            if (theme == THEME_GRAPHITE) {
                return Color.rgb(18, 23, 27);
            }
            return Color.rgb(21, 26, 30);
        }

        private static int getPantoneDockColor(int theme, int stop) {
            ThemePalette palette = getPantonePalette(theme);
            if (palette == null) {
                return Color.rgb(27, 32, 36);
            }
            if (stop == 0) {
                return mixColor(palette.dockColor, Color.WHITE, palette.light ? 0.12f : 0.06f);
            }
            if (stop == 1) {
                return palette.dockColor;
            }
            return mixColor(palette.dockColor, Color.BLACK, palette.light ? 0.10f : 0.18f);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.OPAQUE;
        }
    }

    private static final class SettingsGearDrawable extends Drawable {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float size = Math.min(width, height);
            float centerX = width * 0.5f;
            float centerY = height * 0.5f;
            drawGear(canvas, centerX, centerY + size * 0.025f, size, Color.argb(135, 0, 0, 0), size * 0.018f);
            drawGear(canvas, centerX, centerY, size, Color.rgb(202, 205, 207), 0f);

            canvas.restore();
        }

        private void drawGear(Canvas canvas, float centerX, float centerY, float size, int color, float offset) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.SQUARE);
            paint.setStrokeWidth(size * 0.065f);
            paint.setColor(color);
            float inner = size * 0.31f;
            float outer = size * 0.43f;
            for (int i = 0; i < 18; i++) {
                double angle = Math.PI * 2d * i / 18d;
                float startX = centerX + (float) Math.cos(angle) * inner + offset;
                float startY = centerY + (float) Math.sin(angle) * inner + offset;
                float endX = centerX + (float) Math.cos(angle) * outer + offset;
                float endY = centerY + (float) Math.sin(angle) * outer + offset;
                canvas.drawLine(startX, startY, endX, endY, paint);
            }

            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(size * 0.08f);
            canvas.drawCircle(centerX + offset, centerY + offset, size * 0.28f, paint);

            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centerX + offset, centerY + offset, size * 0.105f, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(size * 0.035f);
            paint.setColor(Color.argb(Color.alpha(color) * 2 / 3, 255, 255, 255));
            canvas.drawArc(centerX - size * 0.26f + offset,
                    centerY - size * 0.26f + offset,
                    centerX + size * 0.26f + offset,
                    centerY + size * 0.26f + offset,
                    220f, 95f, false, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class AppIconDrawable extends Drawable {
        private final Drawable icon;
        private final int plateColor;
        private final int strokeColor;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        AppIconDrawable(Drawable icon, int plateColor, int strokeColor) {
            this.icon = icon;
            this.plateColor = plateColor;
            this.strokeColor = strokeColor;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);

            float size = Math.min(width, height);
            float left = (width - size) * 0.5f;
            float top = (height - size) * 0.5f;
            float radius = size * 0.22f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(82, 0, 0, 0));
            rect.set(left + size * 0.05f, top + size * 0.07f, left + size * 0.95f, top + size * 0.98f);
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setColor(plateColor);
            rect.set(left + size * 0.04f, top + size * 0.03f, left + size * 0.96f, top + size * 0.94f);
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.018f));
            paint.setColor(strokeColor);
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(68, 255, 255, 255));
            rect.set(left + size * 0.12f, top + size * 0.08f, left + size * 0.88f, top + size * 0.38f);
            canvas.drawRoundRect(rect, radius * 0.75f, radius * 0.75f, paint);

            int inset = Math.round(size * 0.20f);
            int iconLeft = Math.round(left) + inset;
            int iconTop = Math.round(top) + inset;
            int iconRight = Math.round(left + size) - inset;
            int iconBottom = Math.round(top + size) - inset;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.draw(canvas);
            canvas.restore();
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            icon.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            icon.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    private static final class DockIconDrawable extends Drawable {
        private final int type;
        private final int baseColor;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final RectF rect = new RectF();

        DockIconDrawable(int type, int baseColor) {
            this.type = type;
            this.baseColor = baseColor;
        }

        @Override
        public void draw(Canvas canvas) {
            int width = getBounds().width();
            int height = getBounds().height();
            if (width <= 0 || height <= 0) {
                return;
            }

            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            float size = Math.min(width, height);
            float inset = size * 0.04f;
            float radius = size * 0.22f;
            rect.set(inset, inset, width - inset, height - inset);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getBaseColor());
            canvas.drawRoundRect(rect, radius, radius, paint);

            paint.setColor(Color.argb(36, 255, 255, 255));
            rect.set(size * 0.12f, size * 0.08f, width - size * 0.12f, size * 0.42f);
            canvas.drawRoundRect(rect, radius * 0.8f, radius * 0.8f, paint);

            paint.setColor(Color.argb(48, 0, 0, 0));
            rect.set(inset, height - size * 0.18f, width - inset, height - inset);
            canvas.drawRoundRect(rect, radius, radius, paint);

            if (type == DOCK_PHONE) {
                drawPhone(canvas, size);
            } else if (type == DOCK_SETTINGS) {
                drawSettings(canvas, size);
            } else {
                drawMessages(canvas, size);
            }
            canvas.restore();
        }

        private int getBaseColor() {
            return baseColor;
        }

        private void drawPhone(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(size * 0.12f);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(Color.WHITE);
            path.reset();
            path.moveTo(size * 0.30f, size * 0.28f);
            path.cubicTo(size * 0.22f, size * 0.42f, size * 0.36f, size * 0.68f, size * 0.58f, size * 0.78f);
            path.cubicTo(size * 0.67f, size * 0.82f, size * 0.75f, size * 0.77f, size * 0.81f, size * 0.67f);
            canvas.drawPath(path, paint);

            paint.setStyle(Paint.Style.FILL);
            rect.set(size * 0.24f, size * 0.22f, size * 0.40f, size * 0.40f);
            canvas.drawRoundRect(rect, size * 0.05f, size * 0.05f, paint);
            rect.set(size * 0.67f, size * 0.64f, size * 0.84f, size * 0.80f);
            canvas.drawRoundRect(rect, size * 0.05f, size * 0.05f, paint);
        }

        private void drawSettings(Canvas canvas, float size) {
            float center = size * 0.5f;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(size * 0.07f);
            paint.setColor(Color.WHITE);
            for (int i = 0; i < 8; i++) {
                double angle = Math.PI * 2d * i / 8d;
                float startX = center + (float) Math.cos(angle) * size * 0.23f;
                float startY = center + (float) Math.sin(angle) * size * 0.23f;
                float endX = center + (float) Math.cos(angle) * size * 0.33f;
                float endY = center + (float) Math.sin(angle) * size * 0.33f;
                canvas.drawLine(startX, startY, endX, endY, paint);
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(size * 0.09f);
            canvas.drawCircle(center, center, size * 0.23f, paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(center, center, size * 0.08f, paint);
        }

        private void drawMessages(Canvas canvas, float size) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            rect.set(size * 0.20f, size * 0.26f, size * 0.80f, size * 0.66f);
            canvas.drawRoundRect(rect, size * 0.11f, size * 0.11f, paint);

            path.reset();
            path.moveTo(size * 0.34f, size * 0.64f);
            path.lineTo(size * 0.28f, size * 0.78f);
            path.lineTo(size * 0.48f, size * 0.66f);
            path.close();
            canvas.drawPath(path, paint);

            paint.setColor(Color.argb(116, 77, 137, 170));
            canvas.drawCircle(size * 0.38f, size * 0.46f, size * 0.035f, paint);
            canvas.drawCircle(size * 0.50f, size * 0.46f, size * 0.035f, paint);
            canvas.drawCircle(size * 0.62f, size * 0.46f, size * 0.035f, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            paint.setColorFilter(colorFilter);
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
