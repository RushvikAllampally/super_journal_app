package com.example.superjournalapp.constants;

import com.example.superjournalapp.dto.QuoteDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationConstants {

    public static String MY_APP_NAME = "superJournalApp";
    public static String FIRST_TIME_USER = "firstTimeUser";
    public static String GRATITUDE_JOURNAL = "Gratitude Journal";
    public static String REFLECTIVE_JOURNAL = "Reflective Journal";
    public static String BULLET_JOURNAL = "Bullet Journal";
    public static String DREAM_JOURNAL = "Dream Journal";
    public static String JOURNAL_ID_INTENT = "journalId";

    //shared preferences
    public static String APP_USER_NAME = "appUserName";
    public static String IS_PASSCODE_ENABLED = "isPasscodeEnabled";
    public static final String STREAK_PREF_KEY = "streak_counter";
    public static final String LAST_ENTRY_DATE_PREF_KEY = "last_entry_date";


    //intent
    public static String IS_NEW_PASSCODE="setNewPasscode";

    //notificationType
    public static String NOTIFICATION_TYPE = "notificationType";
    public static String Gratitude_NOTIFICATION_TYPE = "gratitude";
    public static String DREAM_NOTIFICATION_TYPE = "dream";
    public static String REFLECTIVE_NOTIFICATION_TYPE = "reflective";
    public static String BULLET_NOTIFICATION_TYPE = "bullet";
    public static String AFFIRMATION_NOTIFICATION_TYPE = "affirmation";
    public static String QUOTE_NOTIFICATION_TYPE = "quote";

    //

    public static String[] AFFIRMATIONS =
            {
                    "I am capable of achieving my goals.",
                    "I believe in my abilities.",
                    "I am in control of my thoughts and emotions.",
                    "I am confident and self-assured.",
                    "I am worthy of love and respect.",
                    "I am resilient and can overcome any challenge.",
                    "I am grateful for all the good in my life.",
                    "I radiate positivity and attract positivity in return.",
                    "I am at peace with my past and excited for my future.",
                    "I am a constant work in progress, and that's okay.",
                    "I am open to new opportunities and experiences.",
                    "I am a magnet for success and good fortune.",
                    "I am in charge of my own happiness.",
                    "I am kind and compassionate to myself and others.",
                    "I am healthy, strong, and full of energy.",
                    "I am deserving of all the good things life has to offer.",
                    "I am free from worry and doubt.",
                    "I am in tune with my intuition and trust my inner wisdom.",
                    "I am constantly growing and evolving.",
                    "I am surrounded by love and support.",
                    "I am capable of handling whatever comes my way.",
                    "I am focused and determined in pursuit of my dreams.",
                    "I am a positive influence on those around me.",
                    "I am fearless and ready to take on challenges.",
                    "I am a source of inspiration to others.",
                    "I am prosperous and abundant in every way.",
                    "I am guided by my inner purpose and passion.",
                    "I am confident in my decisions and choices.",
                    "I am at peace with the present moment.",
                    "I am grateful for the lessons and blessings in my life.",
                    "I am free to be myself and express my true feelings.",
                    "I am aligned with my higher self and inner truth.",
                    "I am open to love and allow it to flow into my life.",
                    "I am worthy of success and achievements.",
                    "I am filled with courage and determination.",
                    "I am a beacon of positivity and light.",
                    "I am creating a life I love and deserve.",
                    "I am loved and cherished by those around me.",
                    "I am a master of my own destiny.",
                    "I am grateful for the abundance that surrounds me.",
                    "I am confident and comfortable in my own skin.",
                    "I am open to receiving all the good that life has to offer.",
                    "I am unstoppable and can overcome any obstacle.",
                    "I am a source of strength for myself and others.",
                    "I am at ease and in harmony with the universe.",
                    "I am a reflection of divine love and light.",
                    "I am focused on my goals and dreams.",
                    "I am free from negative thoughts and beliefs.",
                    "I am a magnet for success and prosperity.",
                    "I am deserving of happiness and fulfillment.",
                    "I am open to the infinite possibilities of the universe.",
                    "I am a powerful creator of my reality.",
                    "I am confident in my abilities and talents.",
                    "I am in control of my own life and choices.",
                    "I am grateful for the abundance that flows to me.",
                    "I am free from self-doubt and fear.",
                    "I am open to receiving love and positive energy.",
                    "I am worthy of all the good that comes my way.",
                    "I am a loving and compassionate person.",
                    "I am connected to the wisdom of the universe.",
                    "I am open to the beauty and joy of life.",
                    "I am confident in my uniqueness and individuality.",
                    "I am guided by my inner wisdom and intuition.",
                    "I am deserving of love and respect.",
                    "I am free to express my true self.",
                    "I am aligned with my inner purpose and passion.",
                    "I am grateful for the opportunities that come my way.",
                    "I am a magnet for happiness and positivity.",
                    "I am worthy of success and prosperity.",
                    "I am strong, confident, and capable.",
                    "I am in control of my thoughts and emotions.",
                    "I am open to new possibilities and experiences.",
                    "I am a constant learner and grow with each experience.",
                    "I am surrounded by love and support.",
                    "I am courageous and fearless.",
                    "I am a source of inspiration to others.",
                    "I am prosperous and abundant in all aspects of my life.",
                    "I am confident in my decisions and choices.",
                    "I am at peace with the present moment.",
                    "I am grateful for the lessons and blessings in my life.",
                    "I am free to express my true feelings and emotions.",
                    "I am aligned with my inner truth and authenticity.",
                    "I am open to giving and receiving love.",
                    "I am worthy of all the good things that come my way.",
                    "I am free from worry and doubt.",
                    "I am connected to the wisdom of the universe.",
                    "I am confident and comfortable with who I am.",
                    "I am open to receiving the abundance of life.",
                    "I am unstoppable and can overcome any challenge.",
                    "I am a source of strength and support to others.",
                    "I am in harmony with the flow of life.",
                    "I am a channel for divine love and light.",
                    "I am focused on my goals and dreams.",
                    "I am free from negative thoughts and beliefs."};
    public static List<QuoteDto> QUOTES_ARRAY = new ArrayList<>();

    static {

        QUOTES_ARRAY.add(createQuote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar"));
        QUOTES_ARRAY.add(createQuote("The only way to do great work is to love what you do.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Success is not final, failure is not fatal: It is the courage to continue that counts.", "Winston Churchill"));
        QUOTES_ARRAY.add(createQuote("In three words I can sum up everything I've learned about life: it goes on.", "Robert Frost"));
        QUOTES_ARRAY.add(createQuote("The greatest glory in living lies not in never falling, but in rising every time we fall.", "Nelson Mandela"));
        QUOTES_ARRAY.add(createQuote("Your time is limited, don't waste it living someone else's life.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Life is really simple, but we insist on making it complicated.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("In the end, it's not the years in your life that count. It's the life in your years.", "Abraham Lincoln"));
        QUOTES_ARRAY.add(createQuote("Don't count the days, make the days count.", "Muhammad Ali"));
        QUOTES_ARRAY.add(createQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("The best way to predict the future is to create it.", "Peter Drucker"));
        QUOTES_ARRAY.add(createQuote("Happiness is not something ready-made. It comes from your own actions.", "Dalai Lama"));
        QUOTES_ARRAY.add(createQuote("The only person you are destined to become is the person you decide to be.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The journey of a thousand miles begins with one step.", "Lao Tzu"));
        QUOTES_ARRAY.add(createQuote("Believe you can and you're halfway there.", "Theodore Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Your work is going to fill a large part of your life, and the only way to be truly satisfied is to do what you believe is great work. And the only way to do great work is to love what you do.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Do not dwell in the past, do not dream of the future, concentrate the mind on the present moment.", "Buddha"));
        QUOTES_ARRAY.add(createQuote("Life is what happens when you're busy making other plans.", "John Lennon"));
        QUOTES_ARRAY.add(createQuote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau"));
        QUOTES_ARRAY.add(createQuote("The only thing necessary for the triumph of evil is for good men to do nothing.", "Edmund Burke"));
        QUOTES_ARRAY.add(createQuote("You miss 100% of the shots you don't take.", "Wayne Gretzky"));
        QUOTES_ARRAY.add(createQuote("In the middle of every difficulty lies opportunity.", "Albert Einstein"));
        QUOTES_ARRAY.add(createQuote("To be yourself in a world that is constantly trying to make you something else is the greatest accomplishment.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The only true wisdom is in knowing you know nothing.", "Socrates"));
        QUOTES_ARRAY.add(createQuote("The best revenge is massive success.", "Frank Sinatra"));
        QUOTES_ARRAY.add(createQuote("Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.", "Thomas A. Edison"));
        QUOTES_ARRAY.add(createQuote("If you want to achieve greatness stop asking for permission.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"));
        QUOTES_ARRAY.add(createQuote("The only way to do great work is to love what you do.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Success is not final, failure is not fatal: It is the courage to continue that counts.", "Winston Churchill"));
        QUOTES_ARRAY.add(createQuote("In three words I can sum up everything I've learned about life: it goes on.", "Robert Frost"));
        QUOTES_ARRAY.add(createQuote("The greatest glory in living lies not in never falling, but in rising every time we fall.", "Nelson Mandela"));
        QUOTES_ARRAY.add(createQuote("Your time is limited, don't waste it living someone else's life.", "Steve Jobs"));
        QUOTES_ARRAY.add(createQuote("Life is really simple, but we insist on making it complicated.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("In the end, it's not the years in your life that count. It's the life in your years.", "Abraham Lincoln"));
        QUOTES_ARRAY.add(createQuote("Don't count the days, make the days count.", "Muhammad Ali"));
        QUOTES_ARRAY.add(createQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"));
        QUOTES_ARRAY.add(createQuote("The best way to predict the future is to create it.", "Peter Drucker"));
        QUOTES_ARRAY.add(createQuote("Happiness is not something ready-made. It comes from your own actions.", "Dalai Lama"));
        QUOTES_ARRAY.add(createQuote("The only person you are destined to become is the person you decide to be.", "Ralph Waldo Emerson"));
        QUOTES_ARRAY.add(createQuote("The journey of a thousand miles begins with one step.", "Lao Tzu"));
        QUOTES_ARRAY.add(createQuote("Believe you can and you're halfway there.", "Theodore Roosevelt"));
        QUOTES_ARRAY.add(createQuote("Change your thoughts and you change your world.", "Norman Vincent Peale"));
        QUOTES_ARRAY.add(createQuote("The biggest risk is not taking any risk. In a world that's changing quickly, the only strategy that is guaranteed to fail is not taking risks.", "Mark Zuckerberg"));
        QUOTES_ARRAY.add(createQuote("In the end, we will remember not the words of our enemies, but the silence of our friends.", "Martin Luther King Jr."));
        QUOTES_ARRAY.add(createQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius"));
        QUOTES_ARRAY.add(createQuote("The way to get started is to quit talking and begin doing.", "Walt Disney"));
        QUOTES_ARRAY.add(createQuote("Our greatest weakness lies in giving up. The most certain way to succeed is always to try just one more time.", "Thomas A. Edison"));
        QUOTES_ARRAY.add(createQuote("If you want to achieve greatness stop asking for permission.", "Anonymous"));
        QUOTES_ARRAY.add(createQuote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"));
        QUOTES_ARRAY.add(createQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt"));
        QUOTES_ARRAY.add(createQuote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt"));

    }

    public static QuoteDto createQuote(String quote, String author) {
        return new QuoteDto(quote, author);
    }


    //mood level messages
    public static List<String> VERY_HAPPY_MOOD_MSGS = new ArrayList<>();

    static {
        VERY_HAPPY_MOOD_MSGS.add("Great to hear! Wishing you continued joy !");
        VERY_HAPPY_MOOD_MSGS.add("Your happiness is contagious. Keep shining !");
        VERY_HAPPY_MOOD_MSGS.add("Thrilled for you! May the good vibes last");
        VERY_HAPPY_MOOD_MSGS.add("Fantastic! Keep the positivity flowing");
        VERY_HAPPY_MOOD_MSGS.add("Your joy is a beacon of light. Enjoy the moment !");
    }

    public static List<String> HAPPY_MOOD_MSGS = new ArrayList<>();

    static {
        HAPPY_MOOD_MSGS.add("Choosing happiness is a win! Keep smiling");
        HAPPY_MOOD_MSGS.add("Happiness suits you well! Stay cheerful");
        HAPPY_MOOD_MSGS.add("Your positive energy is awesome !");
        HAPPY_MOOD_MSGS.add("Happy vibes all around. Keep it up !");
        HAPPY_MOOD_MSGS.add("Cheers to your happiness today !");
    }


    public static List<String> NEUTRAL_MOOD_MSGS = new ArrayList<>();

    static {
        NEUTRAL_MOOD_MSGS.add("A neutral day is full of possibilities. Seize them!");
        NEUTRAL_MOOD_MSGS.add("In the middle ground, where surprises happen. Enjoy!");
        NEUTRAL_MOOD_MSGS.add("Every day has its unique charm. Find yours today.");
        NEUTRAL_MOOD_MSGS.add("Embracing the calm. Here's to a peaceful day!");
        NEUTRAL_MOOD_MSGS.add("Neutral is the canvas. Paint it with small joys.");
    }

    public static List<String> SAD_MOOD_MSGS = new ArrayList<>();

    static {
        SAD_MOOD_MSGS.add("Sending warmth during tough times. You're not alone.");
        SAD_MOOD_MSGS.add("It's okay to feel sad. Take your time, breathe.");
        SAD_MOOD_MSGS.add("Sorrow comes and goes. Tomorrow is a new day.");
        SAD_MOOD_MSGS.add("Rainy days make the flowers bloom. Stay strong.");
        SAD_MOOD_MSGS.add("Your feelings are valid. Reach out if you need to talk.");
    }

    public static List<String> VERY_SAD_MOOD_MSGS = new ArrayList<>();

    static {
        VERY_SAD_MOOD_MSGS.add("During tough times, you're not alone. Reach out.");
        VERY_SAD_MOOD_MSGS.add("Sending strength during challenging moments.");
        VERY_SAD_MOOD_MSGS.add("Sadness may linger, but it doesn't define you.");
        VERY_SAD_MOOD_MSGS.add("Dark days pass. Hold on; brighter ones await.");
        VERY_SAD_MOOD_MSGS.add("You're stronger than you know. Take one step at a time.");
    }


    public static Map<String,Integer> NOTIFICATION_TYPE_REQUEST_CODE = new HashMap<>();

    static {
        NOTIFICATION_TYPE_REQUEST_CODE.put("gratitude",6);
        NOTIFICATION_TYPE_REQUEST_CODE.put("reflective",1);
        NOTIFICATION_TYPE_REQUEST_CODE.put("bullet",2);
        NOTIFICATION_TYPE_REQUEST_CODE.put("dream",3);
        NOTIFICATION_TYPE_REQUEST_CODE.put("quote",4);
        NOTIFICATION_TYPE_REQUEST_CODE.put("affirmation",5);
    }

    public static List<String> GRATITUDE_PROMPTS = new ArrayList<>();

    static {
        //list of 21 prompts
        GRATITUDE_PROMPTS.add("What made you smile today?");
        GRATITUDE_PROMPTS.add("Name three things that brought you joy.");
        GRATITUDE_PROMPTS.add("What achievements are you proud of today?");
        GRATITUDE_PROMPTS.add("Describe a person you are thankful to have in your life.");
        GRATITUDE_PROMPTS.add("Reflect on a recent act of kindness you experienced.");
        GRATITUDE_PROMPTS.add("List three things in nature you are grateful for.");
        GRATITUDE_PROMPTS.add("Write about a skill or talent you are thankful for having.");
        GRATITUDE_PROMPTS.add("Share a positive experience from today.");
        GRATITUDE_PROMPTS.add("Reflect on a challenge you faced and the lessons learned.");
        GRATITUDE_PROMPTS.add("Write about a place that brings you peace and gratitude.");
        GRATITUDE_PROMPTS.add("List three small pleasures you enjoyed today.");
        GRATITUDE_PROMPTS.add("Reflect on the support you received from others.");
        GRATITUDE_PROMPTS.add("Write about a moment of personal growth.");
        GRATITUDE_PROMPTS.add("Express gratitude for your health and well-being.");
        GRATITUDE_PROMPTS.add("What made today different from other days?");
        GRATITUDE_PROMPTS.add("Write about a positive change you've noticed recently.");
        GRATITUDE_PROMPTS.add("Reflect on something you learned today.");
        GRATITUDE_PROMPTS.add("List three things you love about yourself.");
        GRATITUDE_PROMPTS.add("Write about a happy memory that brings you joy.");
        GRATITUDE_PROMPTS.add("List things that you are grateful for today?");
        GRATITUDE_PROMPTS.add("Express gratitude for the opportunities in your life.");
    }


}
