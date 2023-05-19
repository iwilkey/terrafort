package dev.iwilkey.terrafort.physics.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

public final class BulletWrapper implements Disposable {
	
	public final static byte KINEMATIC_FLAG = 1 << 1;
	public final static byte STATIC_FLAG = 1 << 2;
	public final static byte DYNAMIC_FLAG = 1 << 3;
	
	private final static short MAX_SUB_STEPS = 5;
	private final static float FIXED_TIME_STEP = 1f / 60;
	
	private final btCollisionConfiguration collisionConfig;
	private final btDispatcher dispatcher;
	private final btBroadphaseInterface broadphase;
	private final btDiscreteDynamicsWorld dynamicsWorld;
	private final btConstraintSolver constraintSolver;
	private final BulletCollisionListener listener;
	private final DebugDrawer debugRenderer;
	
	public boolean debugMode = true;

	public BulletWrapper() {
		Bullet.init();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, 
				broadphase, constraintSolver, collisionConfig);
		listener = new BulletCollisionListener();
		debugRenderer = new DebugDrawer();
		debugRenderer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
		dynamicsWorld.setDebugDrawer(debugRenderer);
		setWorldGravity(new Vector3(0, 0, 0));
	}
	
	public void tick() {
		dynamicsWorld.stepSimulation((float)Gdx.graphics.getDeltaTime(), MAX_SUB_STEPS, FIXED_TIME_STEP);
	}
	
	public void setWorldGravity(Vector3 grav) {
		dynamicsWorld.setGravity(grav);
	}
	
	public BulletCollisionListener getCollisionListener() {
		return listener;
	}
	
	public btDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}
	
	public DebugDrawer getDebugDrawer() {
		return debugRenderer;
	}
	
	@Override
	public void dispose() {
		dynamicsWorld.dispose();
		dispatcher.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		collisionConfig.dispose();
		debugRenderer.dispose();
	}

}
